package com.poulastaa.lms.presentation.auth

import com.poulastaa.lms.navigation.Screens
import com.poulastaa.lms.ui.utils.UiText

sealed interface AuthUiAction {
    data class OnSuccess(val route: Screens) : AuthUiAction
    data class SendToast(val value: UiText) : AuthUiAction
}