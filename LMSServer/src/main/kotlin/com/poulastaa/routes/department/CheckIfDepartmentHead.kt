package com.poulastaa.routes.department

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.checkIfDepartmentHead(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.IsStillDepartmentHead.route) {
            get {
                val payload =
                    call.sessions.get<UserSession>() ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service.isStillDepartmentInCharge(payload.email)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}