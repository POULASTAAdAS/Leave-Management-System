package com.poulastaa.lms.data.model.auth

import com.poulastaa.lms.data.model.home.UserType

data class LocalUser(
    val name: String = "User",
    val email: String = "",
    val profilePicUrl: String? = null,
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val sex: String = "M",
    val isDepartmentInCharge: Boolean = false,
    val userType: UserType = UserType.NON
)
