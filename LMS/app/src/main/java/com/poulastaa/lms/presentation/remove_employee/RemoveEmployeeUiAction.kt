package com.poulastaa.lms.presentation.remove_employee

import com.poulastaa.lms.ui.utils.UiText

sealed interface RemoveEmployeeUiAction {
    data class EmitToast(val message: UiText): RemoveEmployeeUiAction
}