package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
data class AuthRes(
    val authStatus: AuthStatus = AuthStatus.EMAIL_NOT_REGISTERED
)
