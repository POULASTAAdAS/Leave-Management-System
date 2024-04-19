package online.poulastaa.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthReq(
    val email: String
)
