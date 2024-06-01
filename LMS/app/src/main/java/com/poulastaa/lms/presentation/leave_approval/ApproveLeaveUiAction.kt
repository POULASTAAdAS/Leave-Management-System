package com.poulastaa.lms.presentation.leave_approval

import com.poulastaa.lms.ui.utils.UiText

sealed interface ApproveLeaveUiAction {
    data class EmitToast(val message: UiText) : ApproveLeaveUiAction
}