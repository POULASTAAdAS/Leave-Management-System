package online.poulastaa.data.model.auth

import io.ktor.server.auth.*

data class UserSession(
    val email: String,
    val name: String
) : Principal
