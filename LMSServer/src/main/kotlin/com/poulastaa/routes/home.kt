package com.poulastaa.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route


fun Route.home() {
    route("/") {
        get {
            call.respond(
                status = HttpStatusCode.OK,
                message = "Welcome to Leave-Management-System"
            )
        }
    }
}