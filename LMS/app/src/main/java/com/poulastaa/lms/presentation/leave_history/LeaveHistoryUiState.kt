package com.poulastaa.lms.presentation.leave_history

data class LeaveHistoryUiState(
    val header: List<String> = listOf(
        "Request Date",
        "Leave Type",
        "Approved/Rejected\nDate",
        "Status",
        "Pending End",
        "Total Days",
        "From Date",
        "To Date",
    )
)


data class LeaveHistoryInfo(
    val reqDate: String,
    val leaveType: String,
    val approveDate:String,
    val status: String,
    val fromDate: String,
    val toDate: String,
    val pendingEnd: String,
    val totalDays: String,
)