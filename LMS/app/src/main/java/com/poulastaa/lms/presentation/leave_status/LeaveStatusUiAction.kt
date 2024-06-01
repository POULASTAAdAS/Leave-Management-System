package com.poulastaa.lms.presentation.leave_status

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface LeaveStatusUiAction {
    data class OnSuccess(val screens: Screens) : LeaveStatusUiAction
    data class OnErr(val message: UiText) : LeaveStatusUiAction
}