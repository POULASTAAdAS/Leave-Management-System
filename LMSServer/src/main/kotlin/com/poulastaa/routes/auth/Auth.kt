package com.poulastaa.routes.auth

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.req.AuthReq
import com.poulastaa.data.model.auth.res.AuthStatus
import com.poulastaa.data.repository.ServiceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authenUser(
    service: ServiceRepository,
) {
    route(EndPoints.Auth.route) {
        post {
            val req =
                call.receiveNullable<AuthReq>() ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

            val response = service.auth(req.email)

            when (response.authStatus) {
                AuthStatus.EMAIL_NOT_REGISTERED -> {
                    call.respond(
                        message = response,
                        status = HttpStatusCode.NotFound
                    )
                }

                else -> {
                    call.respond(
                        message = response,
                        status = HttpStatusCode.OK
                    )
                }
            }
        }
    }
}