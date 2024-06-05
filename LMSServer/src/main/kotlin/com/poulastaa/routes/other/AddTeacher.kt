package com.poulastaa.routes.other

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.model.auth.req.AddTeacherReq
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Route.addTeacher(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.AddTeachers.route) {
            post {
                call.sessions.get<UserSession>() ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)


                val req = call.receiveNullable<AddTeacherReq>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

                val response = service.addTeacher(req)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}