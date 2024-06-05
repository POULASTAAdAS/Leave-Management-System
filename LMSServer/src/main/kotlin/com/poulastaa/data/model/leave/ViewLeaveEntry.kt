package com.poulastaa.data.model.leave

data class ViewLeaveEntry(
    val name: String,
    val leaveType: String,
    val reqData: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
    val status: String,
    val cause: String,
    val department: String
)