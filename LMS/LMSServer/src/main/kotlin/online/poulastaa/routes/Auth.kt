package online.poulastaa.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import online.poulastaa.data.model.EndPoints
import online.poulastaa.data.model.auth.AuthReq
import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.repository.UserServiceRepository
import online.poulastaa.utils.Constants.SECURITY_LIST


fun Route.auth(service: UserServiceRepository) {
    authenticate(configurations = SECURITY_LIST) {
        route(EndPoints.Auth.route) {
            post {
                val req = call.receiveNullable<AuthReq>() ?: return@post call.respond(
                    message = AuthResponse(),
                    status = HttpStatusCode.OK
                )

                val response = service.auth(req)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}