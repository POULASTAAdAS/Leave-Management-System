package com.poulastaa.routes.auth

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.repository.ServiceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkVerificationMailStatus(
    service: ServiceRepository
) {
    route(EndPoints.EmailVerificationCheck.route) {
        get {
            val email = call.parameters["email"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)


            val response = service.emailVerificationCheck(email)

            call.respond(
                message = response,
                status = HttpStatusCode.OK
            )
        }
    }
}