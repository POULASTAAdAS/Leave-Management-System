package online.poulastaa.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import online.poulastaa.data.model.auth.UserSession
import online.poulastaa.domain.repository.SessionStorageImpl
import online.poulastaa.utils.Constants.SESSION_NAME

fun Application.configureSession() {
    install(Sessions) {
        cookie<UserSession>(
            name = SESSION_NAME,
            SessionStorageImpl()
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