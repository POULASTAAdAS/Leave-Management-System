package com.poulastaa.routes.leave

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.utils.setCookie
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.viewLeaves(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.GetViewLeaves.route) {
            get {
                val payload =
                    call.sessions.get<UserSession>() ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val page = call.parameters["page"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)
                val pageSize =
                    call.parameters["pageSize"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)


                val response = service.viewLeave(
                    email = payload.email,
                    page = page.toInt(),
                    pageSize = pageSize.toInt()
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