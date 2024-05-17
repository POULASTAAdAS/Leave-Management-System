package com.poulastaa.plugins

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.utils.Constants.AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*

fun Application.configureSecurity() {
    install(Authentication) {
        session<UserSession>(AUTH) {
            validate { it }

            challenge { call.resolveResource(EndPoints.UnAuthorised.route) }
        }
    }
}