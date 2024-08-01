package com.poulastaa.routes.report

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getReport(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.GetReport.route) {
            get {
                val department =
                    call.parameters["department"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val leaveType =
                    call.parameters["leaveType"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val teacher =
                    call.parameters["teacher"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

                val result = service.getReport(
                    department = department,
                    type = leaveType,
                    teacher = teacher
                )

                call.respond(result)
            }
        }
    }
}