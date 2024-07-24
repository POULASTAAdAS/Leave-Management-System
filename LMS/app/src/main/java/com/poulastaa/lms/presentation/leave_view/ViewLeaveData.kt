package com.poulastaa.lms.presentation.leave_view

data class ViewLeaveSingleData(
    val isExpanded: Boolean = true,
    val department: String,
    val listOfLeave: List<ViewLeaveInfo>,
)


data class ViewLeaveInfo(
    val reqData: String,
    val name: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
    val status: String,
    val cause: String,
)