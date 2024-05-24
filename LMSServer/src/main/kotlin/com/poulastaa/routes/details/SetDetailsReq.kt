package com.poulastaa.routes.details

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.TeacherDetailsSaveStatus
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.utils.setCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.setDetailsReq(
    service: ServiceRepository
) {
    route(EndPoints.SetDetails.route) {
        post {
            val req = call.receiveNullable<SetDetailsReq>()
                ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)

            val response = service.saveTeacherDetails(req)

            if (response.status != TeacherDetailsSaveStatus.INVALID_REQ &&
                response.status != TeacherDetailsSaveStatus.NOT_REGISTERED
            ) setCookie(
                email = req.email,
                name = req.name
            )

            call.respond(
                message = response,
                status = HttpStatusCode.OK
            )
        }
    }
}

