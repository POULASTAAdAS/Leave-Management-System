package com.poulastaa.lms.presentation.leave_approval

import androidx.compose.runtime.Stable
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.presentation.store_details.Holder
import com.poulastaa.lms.presentation.store_details.ListHolder

data class ApproveLeaveUiState(
    val isMakingApiCall: Boolean = false,
    val userType: UserType = UserType.LOAD,
    val header: String = ""
)

@Stable
data class LeaveApproveCardInfo(
    val id: Long = -1,
    val isActionExpanded: Boolean = false,
    val reqDate: String = "",
    val name: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val leaveType: String = "",
    val totalDays: String = "",
    val docUrl: String? = null,
    val isImageExpanded: Boolean = false,
    val focusId: Long = -1,
    val isImageFocused: Boolean = false,
    val actions: ListHolder = ListHolder(
        all = listOf(
            "Accept And Forward",
            "Reject"
        )
    ),
    val isRejected: Boolean = false,
    val cause: Holder = Holder(),
    val isSendingDataToServer: Boolean = false,
)
