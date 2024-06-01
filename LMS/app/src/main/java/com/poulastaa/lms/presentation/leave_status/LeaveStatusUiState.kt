package com.poulastaa.lms.presentation.leave_status

import androidx.compose.runtime.Stable
import com.poulastaa.lms.presentation.store_details.ListHolder

data class LeaveStatusUiState(
    val isMakingApiCall: Boolean = false,

    val leaveTypes: ListHolder = ListHolder(),
    val balance: String = "0.0"
)
