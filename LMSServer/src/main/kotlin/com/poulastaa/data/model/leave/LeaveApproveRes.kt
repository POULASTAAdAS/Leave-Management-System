package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class LeaveApproveRes(
    val leaveId: Long,
    val reqData: String,
    val name: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
    val docUrl: String?,
)
