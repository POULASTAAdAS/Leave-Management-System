package com.poulastaa.data.model.auth.res

import com.poulastaa.data.model.constants.TeacherType
import kotlinx.serialization.Serializable

@Serializable
data class AuthRes(
    val authStatus: AuthStatus = AuthStatus.EMAIL_NOT_REGISTERED,
    val teacherTypeId: TeacherType = TeacherType.NON
)
