package com.poulastaa.lms.presentation.leave_history

data class LeaveHistoryUiState(
    val isMakingApiCall: Boolean = false,

    val listOfLeaveInfo: List<LeaveInfo> = emptyList()
)


data class LeaveInfo(
    val reqDate: String = "",
    val leaveType: String = "",
    val status: String = "",
    val pendingEnd: String = ""
)