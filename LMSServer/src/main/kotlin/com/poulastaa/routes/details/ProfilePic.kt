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
import java.nio.file.Paths

fun Route.updateProfile(service: ServiceRepository) {
    authenticate("session-auth") {
        route(EndPoints.UpdateProfilePic.route) {
            post {
                val payload = call.sessions.get<UserSession>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val fileData = call.receiveMultipart()

                var status = false

                fileData.forEachPart {
                    when (it) {
                        is PartData.FileItem -> {
                            val dir = File(System.getenv("profileFolder").dropLast(1))

                            dir.listFiles { _, name -> // delete old file
                                name.startsWith("${payload.name}_${payload.email}")
                            }?.forEach { file ->
                                file.delete()
                            }

                            val fileName = "${payload.name}_${payload.email}_${it.originalFileName}"
                            val path = Paths.get(System.getenv("profileFolder"), fileName).toString()

                            val fileBytes = it.streamProvider().readBytes()
                            File(path).writeBytes(fileBytes) // create new file

                            status = service.storeProfilePic(
                                email = payload.email,
                                name = fileName
                            )
                        }

                        else -> Unit
                    }

                    it.dispose()
                }


                call.respond(
                    message = status,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}

fun Route.getProfilePic(service: ServiceRepository) {
    authenticate("session-auth") {
        route(EndPoints.GetProfilePic.route)
        {
            get {
                val payload = call.sessions.get<UserSession>()
                    ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                service.getProfilePic(payload.email)?.let { file ->
                    call.respondFile(file)
                } ?: call.respond(
                    HttpStatusCode.NotFound
                )
            }
        }
    }
}

fun Route.getImage() {
    authenticate("session-auth") {
        route(EndPoints.GetImage.route) {
            get {
                val query = call.parameters["profile"] ?: return@get
                try {
                    val file = File("${System.getenv("profileFolder")}$query")

                    call.respondFile(file)
                } catch (_: Exception) {
                    call.respond("")
                }
            }
        }
    }
}