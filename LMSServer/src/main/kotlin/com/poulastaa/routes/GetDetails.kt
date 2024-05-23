package com.poulastaa.routes

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.utils.setCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.getDetails(
    service: ServiceRepository
) {
    authenticate("session-auth") {
        route(EndPoints.GetDetails.route) {
            get {
                val payload =
                    call.sessions.get<UserSession>() ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val email = call.parameters["email"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service.getTeacherDetails(email) ?: return@get call.respond(
                    message = GetTeacherRes(),
                    status = HttpStatusCode.NotFound
                )

                setCookie(
                    email = payload.email,
                    name = payload.name
                )

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}