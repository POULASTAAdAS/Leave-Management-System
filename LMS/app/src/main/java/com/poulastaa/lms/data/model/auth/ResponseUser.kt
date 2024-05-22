package com.poulastaa.lms.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser(
    val name: String = "",
    val email: String = "",
    val profilePicUrl: String? = null,
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val isDepartmentInCharge: Boolean = false
)