package com.poulastaa.routes.auth

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.res.UnAuth
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.unAuth() {
    route(EndPoints.UnAuthorised.route) {
        get {
            call.respond(
                message = UnAuth(),
                status = HttpStatusCode.Forbidden
            )
        }
    }
}