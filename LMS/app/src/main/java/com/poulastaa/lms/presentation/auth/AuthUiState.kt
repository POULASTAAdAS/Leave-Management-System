package com.poulastaa.lms.presentation.auth

import com.poulastaa.lms.data.model.auth.EndPoints
import com.poulastaa.lms.ui.utils.UiText

data class AuthUiState(
    val email: String = "",
    val isValidEmail: Boolean = false,

    val isEmailErr: Boolean = false,
    val emailErr: UiText = UiText.DynamicString(""),

    val isMakingApiCall: Boolean = false,

    val isInternet: Boolean = false,

    val isEmailHintVisible: Boolean = true,

    val resendVerificationEmailVisible: Boolean = false,
    val resendVerificationEmailButtonEnabled: Boolean = false,
    val resendVerificationEmailButtonText: String = "S E N D",

    val route: String = EndPoints.LogInEmailVerificationCheck.route
)
