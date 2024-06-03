package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class HandleLeaveReq(
    val leaveId: Long,
    val action: String,
    val cause: String
)
