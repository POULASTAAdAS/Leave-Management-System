package com.poulastaa.lms.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class ApplyLeaveRes(
    val status: ApplyLeaveStatus,
    val newBalance: String
)
