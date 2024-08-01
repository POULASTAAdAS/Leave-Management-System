package com.poulastaa.lms.data.model.leave

data class LeaveHistoryInfoRes(
    val reqDate: String = "",
    val leaveType: String = "",
    val approveDate: String = "",
    val status: String = "",
    val fromDate: String,
    val toDate: String,
    val pendingEnd: String = "",
    val totalDays: String = ""
)
