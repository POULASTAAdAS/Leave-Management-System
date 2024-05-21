package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
enum class TeacherDetailsSaveStatus {
    SAVED,
    ALREADY_SAVED,
    INVALID_REQ,
    NOT_REGISTERED
}