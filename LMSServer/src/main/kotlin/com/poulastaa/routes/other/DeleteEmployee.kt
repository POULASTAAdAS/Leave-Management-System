package com.poulastaa.routes.other

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.other.DeleteTeacherReq
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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

fun Route.deleteTeacher(service: ServiceRepository) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.DeleteTeacher.route) {
            post {
                val req = call.receiveNullable<DeleteTeacherReq>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val result = service.deleteTeacher(req)

                when (result) {
                    true -> call.respond(HttpStatusCode.OK)
                    false -> call.respond(HttpStatusCode.ServiceUnavailable)
                }
            }
        }
    }
}