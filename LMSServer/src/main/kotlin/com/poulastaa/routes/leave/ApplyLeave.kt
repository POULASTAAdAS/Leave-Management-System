package com.poulastaa.routes.leave

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.data.model.leave.ApplyLeaveStatus
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Route.applyLeave(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.ApplyLeave.route) {
            post {
                val payload =
                    call.sessions.get<UserSession>() ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val multiPartData = call.receiveMultipart()
                var req: ApplyLeaveReq? = null
                var filePath: String? = null

                multiPartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            if (part.name == "json") req = Json.decodeFromString(
                                ApplyLeaveReq.serializer(),
                                part.value
                            )
                        }

                        is PartData.FileItem -> {
                            val fileName = "${payload.name}_${payload.email}_${
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh-mm"))
                            }_${part.originalFileName}"

                            val path = Paths.get(Constants.MEDICAL_FOLDER_PATH, fileName).toString()

                            val fileBytes = part.streamProvider().readBytes()
                            File(path).writeBytes(fileBytes) // create new file

                            filePath = fileName
                        }

                        else -> Unit
                    }

                    part.dispose()
                }

                val response = req?.let {
                    service.handleLeaveReq(
                        req = it,
                        filePath = filePath
                    )
                } ?: return@post call.respond(
                    message = ApplyLeaveRes(
                        status = ApplyLeaveStatus.REJECTED
                    ),
                    status = HttpStatusCode.OK
                )

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}