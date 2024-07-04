package com.poulastaa.routes.leave

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.model.leave.HandleLeaveReq
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.utils.setCookie
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.handleLeave(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.HandleLeave.route) {
            post {
                val payload =
                    call.sessions.get<UserSession>() ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val req = call.receiveNullable<HandleLeaveReq>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service.handleLeave(
                    req = req,
                    email = payload.email
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