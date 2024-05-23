package com.poulastaa.plugins

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*

fun Application.configureSecurity() {
    install(Authentication) {
        session<UserSession>("session-auth") {
            validate { if (it.email.isNotEmpty()) it else null }

            challenge { call.resolveResource(EndPoints.UnAuthorised.route) }
        }
    }
}