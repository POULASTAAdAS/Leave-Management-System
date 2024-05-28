package com.poulastaa.routes.leave

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.applyLeave(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.ApplyLeave.route) {
            post {
                val payload =
                    call.sessions.get<UserSession>() ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val req = call.receiveNullable<ApplyLeaveReq>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service
            }
        }
    }
}