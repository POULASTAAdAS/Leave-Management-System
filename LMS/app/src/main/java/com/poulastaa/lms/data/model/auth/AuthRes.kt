package com.poulastaa.lms.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRes(
    val authStatus: AuthStatus = AuthStatus.EMAIL_NOT_REGISTERED,
    val user: ResponseUser = ResponseUser()
)

