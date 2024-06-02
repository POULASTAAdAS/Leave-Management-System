package com.poulastaa.lms.data.model.leave

data class LeaveInfoRes(
    val reqDate: String = "",
    val leaveType: String = "",
    val status: String = "",
    val fromDate: String,
    val toDate: String,
    val pendingEnd: String = "",
)
