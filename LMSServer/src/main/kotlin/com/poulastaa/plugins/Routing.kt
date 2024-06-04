package com.poulastaa.plugins

import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.auth.*
import com.poulastaa.routes.details.*
import com.poulastaa.routes.leave.*
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

        verifySignUpEmail(service)
        verifyLogInEmail(service)

        checkSignUpVerificationMailStatus(service)
        checkLoginVerificationMailStatus(service)

        setDetailsReq(service)
        getDetails(service)

        updateDetails(service)
        updateHeadDetails(service)
        updateAddress(service)

        updateProfile(service)
        getProfilePic(service)

        getLeaveBalance(service)
        applyLeave(service)
        getLeave(service)

        getApproveLeave(service)
        handleLeave(service)

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