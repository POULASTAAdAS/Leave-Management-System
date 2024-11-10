package com.poulastaa.routes.report

import com.itextpdf.html2pdf.HtmlConverter
import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.other.PdfData
import com.poulastaa.data.model.other.PdfReportData
import com.poulastaa.data.model.other.PdfTeacher
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

                val result = service.generatePdfData(
                    department = department,
                    type = leaveType,
                    teacher = teacher
                )

                val html = generateHtml(result)

                call.respondBytes(
                    bytes = convertHtmlToPdf(html),
                    contentType = ContentType.Application.Pdf,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}

private fun convertHtmlToPdf(htmlContent: String): ByteArray {
    val byteArrayOutputStream = com.itextpdf.io.source.ByteArrayOutputStream()
    HtmlConverter.convertToPdf(htmlContent, byteArrayOutputStream)

    return byteArrayOutputStream.toByteArray()
}


private fun generateHtml(list: List<PdfData>) = """
        <!DOCTYPE html>
        <html lang="en">
        
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Leave Report</title>
            <style>
                body {
                    padding: 0 20px;
                }
        
                .header,
                .report {
                    text-align: center;
                }
        
                .info {
                    text-align: left;
                }
        
                .report table {
                    width: 100%;
                    border-collapse: collapse;
                }
        
                .report th,
                .report td {
                    padding: 8px;
                }
        
                .footer {
                    text-align: center;
                    margin-top: 50px;
                    font-size: 14px;
                }
            </style>
        </head>
        
        <body>
            <div>
                <div class="header">
                    <img src="./src/main/resources/college_logo.png" alt="College Logo">
                    <h1>Bhairab Ganguly College</h1>
                    <h2 style="text-decoration: underline;">Leave Report</h2>
                    <h3>Generation Date - ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY"))}</h3>
                </div>
        
               ${
    list.joinToString {
        generateHTMLForDepartment(it)
    }.trimIndent().replace(",", "\n")
}
                <br>
                <br>
        
                <div class="footer">
                    <p>System generated document. No need for signature.</p>
            </div>
        </body>
        
        </html>
    """.trimIndent()


private fun generateHTMLForDepartment(data: PdfData) = """
        <br>
        <br>
        
       <div class="info">
           <h1>Department - ${data.department}</h1>
           <h3>Name – ${data.listOfData.first().name}</h3>
           <h3>Designation – ${data.listOfData.first().designation}</h3>
       </div>
       
       <br>
       
       <div class="report">
            <table border=".5" align="center">
                <thead>
                    <tr>
                        <th>Application Date</th>
                        <th>Leave Type</th>
                        <th>From Date</th>
                        <th>To Date</th>
                        <th>Total Days</th>
                        <th>Reason</th>
                    </tr>
                </thead>
                ${generateTable(data.listOfData.first().listOfData)}
            </table>
       </div>
       
       ${generateEntry(data.listOfData.drop(1))}
       
""".trimIndent()

private fun generateEntry(data: List<PdfTeacher>) = data.joinToString {
    """
        <br>  
        <br>
                
        <div class="info">
        <h3>Name – ${it.name}</h3>
        <h3>Designation - ${it.designation}</h3>
        </div>
        
        <div class="report">
            <table border=".5" align="center">
                <thead>
                    <tr>
                        <th>Application Date</th>
                        <th>Leave Type</th>
                        <th>From Date</th>
                        <th>To Date</th>
                        <th>Total Days</th>
                        <th>Reason</th>
                    </tr>
                </thead>
                ${generateTable(data.first().listOfData)}
            </table>
       </div>
    """.trimIndent()
}

private fun generateTable(list: List<PdfReportData>) = list.joinToString {
    """
        <tbody align="center">
              <tr>
                  <td>${it.applicationDate}</td>
                  <td>${it.leaveType}</td>
                  <td>${it.fromDate}</td>
                  <td>${it.toDate}</td>
                  <td>${it.totalDays}</td>
                  <td>${it.status}</td>
              </tr>
        </tbody>
    """.trimIndent()
}
