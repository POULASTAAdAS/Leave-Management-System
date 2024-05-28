package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
enum class ApplyLeaveStatus {
    ACCEPTED,
    REJECTED,
    A_REQ_HAS_ALREADY_EXISTS,
    SOMETHING_WENT_WRONG
}