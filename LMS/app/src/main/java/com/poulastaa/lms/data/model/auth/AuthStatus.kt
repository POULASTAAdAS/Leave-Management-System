package com.poulastaa.lms.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
enum class AuthStatus {
    PRINCIPLE_FOUND,
    HEAD_CLARK_FOUND,
    LOGIN,
    SIGNUP,
    EMAIL_NOT_REGISTERED
}