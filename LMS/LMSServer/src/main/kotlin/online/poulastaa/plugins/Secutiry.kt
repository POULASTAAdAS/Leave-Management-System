package online.poulastaa.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import online.poulastaa.data.model.EndPoints
import online.poulastaa.data.model.auth.UserSession
import online.poulastaa.utils.Constants

fun Application.configureSecurity() {
    install(Authentication) {
        session<UserSession>(Constants.SECURITY_LIST[0]) {
            validate { it }

            challenge { call.resolveResource(EndPoints.UnAuthorised.route) }
        }
    }
}