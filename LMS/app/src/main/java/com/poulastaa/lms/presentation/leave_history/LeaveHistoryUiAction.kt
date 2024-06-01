package com.poulastaa.lms.presentation.leave_history

import com.poulastaa.lms.ui.utils.UiText

sealed interface LeaveHistoryUiAction {
    data class OnErr(val message: UiText) : LeaveHistoryUiAction
}