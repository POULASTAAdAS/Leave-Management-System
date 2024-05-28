package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
enum class ApplyLeaveStatus {
    ACCEPTED,
    REJECTED,
    SOMETHING_WENT_WRONG
}