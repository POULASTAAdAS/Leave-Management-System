package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class LeaveHistoryRes(
    val reqDate: String,
    val leaveType: String,
    val approveDate: String,
    val status: String,
    val fromDate: String,
    val toDate: String,
    val pendingEnd: String,
    val totalDays: String,
)
