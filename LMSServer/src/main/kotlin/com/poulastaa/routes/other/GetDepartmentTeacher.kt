package com.poulastaa.routes.other

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getDepartmentTeacher(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.GetDepartmentTeachers.route) {
            get {
                val departments =
                    call.parameters["department"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val result = service.getDepartmentTeacher(departments)

                call.respond(
                    message = result,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}