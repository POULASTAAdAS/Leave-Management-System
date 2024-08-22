package com.poulastaa.routes.other

import com.poulastaa.data.model.EndPoints
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.getDoc() {
    authenticate(SESSION_AUTH) {
        route(EndPoints.GetDoc.route) {
            get {
                val image = call.parameters["image"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                try {
                    call.respondFile(File("${System.getenv("medicalFolder")}/$image"))
                } catch (_: Exception) {
                    call.respondRedirect(EndPoints.UnAuthorised.route)
                }
            }
        }
    }

}