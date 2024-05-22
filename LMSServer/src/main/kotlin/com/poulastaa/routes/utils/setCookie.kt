package com.poulastaa.routes.utils

import com.poulastaa.data.model.auth.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

fun PipelineContext<Unit, ApplicationCall>.setCookie(email: String, name: String) {
    call.sessions.set(
        UserSession(
            name = name,
            email = email,
        )
    )
}