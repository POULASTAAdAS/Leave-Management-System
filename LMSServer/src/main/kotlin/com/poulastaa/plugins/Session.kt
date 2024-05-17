package com.poulastaa.plugins

import com.poulastaa.data.model.auth.UserSession
import com.poulastaa.domain.repository.SessionStorageDatabaseImpl
import com.poulastaa.utils.Constants.DEFAULT_SESSION_MAX_AGE
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<UserSession>(
            name = "user_session",
            SessionStorageDatabaseImpl()
        ) {
            transform(
                SessionTransportTransformerEncrypt(
                    hex(System.getenv("sessionEncryptionKey")),
                    hex(System.getenv("sessionSecretKey"))
                )
            )

            cookie.maxAgeInSeconds = DEFAULT_SESSION_MAX_AGE
        }
    }
}