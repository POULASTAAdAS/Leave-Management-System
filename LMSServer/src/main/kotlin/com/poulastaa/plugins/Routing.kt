package com.poulastaa.plugins

import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.auth.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import java.io.File

fun Application.configureRouting() {
    val service: ServiceRepository by inject()

    routing {
        interceptor()

        authenUser(service)
        verifyEmail(service)
        checkVerificationMailStatus(service)
        setDetailsReq(service)

        unAuth()

        staticFiles(
            remotePath = ".well-known",
            dir = File("certs")
        )
    }
}


private fun Routing.interceptor() {
    intercept(ApplicationCallPipeline.Call) {
        call.sessions.get<UserSession>()?.let {
            call.sessions.set(it)
        }
    }
}