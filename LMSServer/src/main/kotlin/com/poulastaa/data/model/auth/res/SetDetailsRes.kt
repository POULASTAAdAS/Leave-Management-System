package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
data class SetDetailsRes(
    val status: TeacherDetailsSaveStatus = TeacherDetailsSaveStatus.INVALID_REQ,
    val isDepartmentHead: Boolean = false
)
