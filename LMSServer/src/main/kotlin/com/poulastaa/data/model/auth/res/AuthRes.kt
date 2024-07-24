package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
data class AuthRes(
    val authStatus: AuthStatus = AuthStatus.EMAIL_NOT_REGISTERED,
    val user: User = User(),
)


@Serializable
data class User(
    val name: String = "",
    val email: String = "",
    val profilePicUrl: String = "",
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val isDepartmentInCharge: Boolean = false,
)