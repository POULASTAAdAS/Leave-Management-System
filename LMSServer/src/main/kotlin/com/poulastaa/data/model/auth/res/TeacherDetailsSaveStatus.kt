package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
enum class TeacherDetailsSaveStatus {
    SAVED,
    ALREADY_SAVED,
    INVALID_REQ,
    NOT_REGISTERED
}