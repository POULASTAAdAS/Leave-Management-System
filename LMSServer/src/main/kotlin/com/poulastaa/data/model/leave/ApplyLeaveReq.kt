package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class ApplyLeaveReq(
    val email: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val reason: String,
    val addressDuringLeave: String,
    val path: String
)
