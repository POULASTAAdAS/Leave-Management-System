package com.poulastaa.lms.presentation.define_in_charge

import com.poulastaa.lms.ui.utils.UiText

sealed interface DefneInChargeUiAction {
    data class EmitToast(val message: UiText) : DefneInChargeUiAction
}