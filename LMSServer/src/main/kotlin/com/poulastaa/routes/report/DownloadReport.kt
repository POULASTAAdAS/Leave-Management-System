package com.poulastaa.routes.report

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.report.ReportResponse
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.ByteArrayOutputStream

fun Route.downloadReport(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.DownloadReport.route) {
            get {
                val department =
                    call.parameters["department"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val leaveType =
                    call.parameters["leaveType"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val teacher =
                    call.parameters["teacher"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val result = service.getReport(
                    department = department,
                    type = leaveType,
                    teacher = teacher
                )

                call.respondBytes(
                    bytes = generatePdf(result),
                    contentType = ContentType.Application.Pdf,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}

private fun addNewPage(document: PDDocument, contentStream: PDPageContentStream): PDPageContentStream {
    contentStream.endText()
    contentStream.close()

    val newPage = PDPage(PDRectangle.A4)
    document.addPage(newPage)
    val newContentStream = PDPageContentStream(document, newPage)
    newContentStream.beginText()
    newContentStream.setFont(PDType1Font.HELVETICA, 10f)
    newContentStream.setLeading(14.5f)
    newContentStream.newLineAtOffset(25f, 750f)
    return newContentStream
}

private fun generatePdf(reports: List<ReportResponse>): ByteArray {
    val outputStream = ByteArrayOutputStream()

    PDDocument().use { document ->
        val page = PDPage(PDRectangle.A4)
        document.addPage(page)
        var contentStream = PDPageContentStream(document, page)
        contentStream.beginText()
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
        contentStream.setLeading(14.5f)
        contentStream.newLineAtOffset(25f, 750f)

        var currentYPosition = 750f
        val marginBottom = 70f
        val lineHeight = 14.5f

        for (reportResponse in reports) {
            // Write department
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f)
            reportResponse.department?.let {
                contentStream.showText("Department: $it")
                contentStream.newLine()
                currentYPosition -= lineHeight
            }

            // Write name
            contentStream.showText("Name: ${reportResponse.name}")
            contentStream.newLine()
            currentYPosition -= lineHeight
            contentStream.newLine()
            currentYPosition -= lineHeight

            // Write Leave Data header
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10f)
            contentStream.showText("  Application Date    Request Type      From Date       To Date            Total Days")
            contentStream.newLine()
            currentYPosition -= lineHeight
            contentStream.setFont(PDType1Font.HELVETICA, 10f)

            // Write each leave entry
            for (leave in reportResponse.listOfLeave) {
                if (currentYPosition < marginBottom) {
                    contentStream = addNewPage(document, contentStream)
                    currentYPosition = 750f
                }

                val applicationDate = leave.applicationDate.padEnd(20)
                val reqType = leave.reqType.padEnd(18)
                val fromDate = leave.fromDate.padEnd(18)
                val toDate = leave.toDate.padEnd(18)
                val totalDays = leave.totalDays.padEnd(14)

                val leaveText = "$applicationDate$reqType$fromDate$toDate$totalDays"
                contentStream.showText(leaveText)
                contentStream.newLine()
                currentYPosition -= lineHeight
            }

            contentStream.newLine()
            contentStream.newLine()
            contentStream.newLine()
            currentYPosition -= lineHeight
        }

        contentStream.endText()
        contentStream.close()

        document.save(outputStream)
    }

    return outputStream.toByteArray()
}
