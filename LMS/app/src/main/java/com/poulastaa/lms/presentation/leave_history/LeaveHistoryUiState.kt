package com.poulastaa.lms.presentation.leave_history

data class LeaveHistoryUiState(
    val isMakingApiCall: Boolean = false,
    val header: List<String> = listOf(
        "Request Date",
        "Leave Type",
        "Status",
        "Pending End",
        "Total Days",
        "From Date",
        "To Date",
    )
)


data class LeaveInfo(
    val reqDate: String,
    val leaveType: String,
    val status: String,
    val fromDate: String,
    val toDate: String,
    val pendingEnd: String,
    val totalDays: String,
)