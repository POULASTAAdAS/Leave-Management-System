package com.poulastaa.lms.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
enum class AuthStatus {
    LOGIN,
    SIGNUP,
    EMAIL_NOT_REGISTERED
}