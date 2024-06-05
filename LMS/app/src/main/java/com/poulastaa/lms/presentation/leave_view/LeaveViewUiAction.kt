package com.poulastaa.lms.presentation.leave_view

import com.poulastaa.lms.ui.utils.UiText

sealed interface LeaveViewUiAction {
    data class EmitToast(val message: UiText) : LeaveViewUiAction
}