package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class ApplyLeaveRes(
    val status: ApplyLeaveStatus,
    val newBalance: String
)