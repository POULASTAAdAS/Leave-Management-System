package com.poulastaa.lms.presentation.update_balnce

import com.poulastaa.lms.ui.utils.UiText

sealed interface UpdateBalanceUiAction {
    data class EmitToast(val massage: UiText) : UpdateBalanceUiAction
}