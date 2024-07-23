package com.poulastaa.routes.other

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.other.UpdateLeaveBalanceReq
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.utils.Constants.SESSION_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.updateTeacherLeaveBalance(
    service: ServiceRepository,
) {
    authenticate(SESSION_AUTH) {
        route(EndPoints.UpdateTeacherLeaveBalance.route) {
            post {
                val req = call.receiveNullable<UpdateLeaveBalanceReq>()
                    ?: return@post call.respondRedirect(EndPoints.UnAuthorised.route)


                val result = service.updateLeaveBalance(req)

                call.respond(result)
            }
        }
    }
}