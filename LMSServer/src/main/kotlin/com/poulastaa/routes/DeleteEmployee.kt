package com.poulastaa.routes

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getTeacherToDelete(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.GetTeacherToDelete.route) {
            get {
                val department =
                    call.parameters["department"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service.getTeacherToDelete(department)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}