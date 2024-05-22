package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
data class SetDetailsRes(
    val status: TeacherDetailsSaveStatus = TeacherDetailsSaveStatus.INVALID_REQ,
    val isDepartmentInCharge: Boolean
    // todo add response
)