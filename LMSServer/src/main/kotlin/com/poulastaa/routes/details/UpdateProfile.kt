package com.poulastaa.routes.details

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.io.File

fun Route.updateProfile(service: ServiceRepository) {
    authenticate("session-auth") {
        route(EndPoints.UpdateProfilePic.route) {
            post {
                val payload = call.sessions.get<UserSession>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val fileData = call.receiveMultipart()

                fileData.forEachPart {
                    when (it) {
                        is PartData.FileItem -> {
                            val fileName = it.originalFileName as String
                            val fileBytes = it.streamProvider().readBytes()

                            File("profile/$fileName").writeBytes(fileBytes)

                            val url = service.storeProfilePic(
                                email = payload.email,
                                name = fileName,
                                profilePic = fileBytes
                            )

                            call.respond(
                                message = url,
                                status = HttpStatusCode.OK
                            )
                        }

                        else -> Unit
                    }

                    it.dispose
                }
            }
        }
    }
}