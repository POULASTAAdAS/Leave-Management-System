package com.poulastaa.lms.presentation.leave_approval

import com.poulastaa.lms.presentation.store_details.Holder
import com.poulastaa.lms.presentation.store_details.ListHolder

data class ApproveLeaveUiState(
    val isMakingApiCall: Boolean = false,
    val actions: ListHolder = ListHolder(
        all = listOf(
            "Accept And Forward",
            "Reject"
        )
    ),
    val cause: Holder = Holder(),

    val data: List<LeaveReqInfo> = emptyList()
)

data class LeaveReqInfo(
    val id: Int = -1,
    val isSelected: Boolean = false,
    val reqDate: String = "",
    val name: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val leaveType: String = ""
)
