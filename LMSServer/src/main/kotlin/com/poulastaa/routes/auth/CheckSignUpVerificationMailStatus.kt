package com.poulastaa.routes.auth

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.repository.ServiceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkSignUpVerificationMailStatus(
    service: ServiceRepository
) {
    route(EndPoints.SignUpEmailVerificationCheck.route) {
        get {
            val email = call.parameters["email"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

            val response = service.signUpEmailVerificationCheck(email)

            call.respond(
                message = response,
                status = HttpStatusCode.OK
            )
        }
    }
}

fun Route.checkLoginVerificationMailStatus(
    service: ServiceRepository
) {
    route(EndPoints.LogInEmailVerificationCheck.route) {
        get {
            val email = call.parameters["email"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

            val response = service.loginEmailVerificationCheck(email)

            // todo set cookie if true

            call.respond(
                message = response,
                status = HttpStatusCode.OK
            )
        }
    }
}