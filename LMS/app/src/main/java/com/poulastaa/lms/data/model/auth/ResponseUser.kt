package com.poulastaa.lms.data.model.auth

import com.poulastaa.lms.BuildConfig
import kotlinx.serialization.Serializable

@Serializable
data class ResponseUser(
    val name: String = "",
    val email: String = "",
    val profilePicUrl: String = BuildConfig.BASE_URL + EndPoints.GetProfilePic.route,
    val phone: String = "",
    val department: String = "",
    val designation: String = "",
    val isDepartmentInCharge: Boolean = false
)