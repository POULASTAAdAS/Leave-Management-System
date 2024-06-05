package com.poulastaa.lms.presentation.add_teacher

import com.poulastaa.lms.ui.utils.UiText

sealed interface AddTeacherUiAction {
    data class EmitToast(val message: UiText) : AddTeacherUiAction
}