package com.poulastaa.lms.presentation.auth

sealed interface AuthUiEvent {
    data class EmailChanged(val value: String) : AuthUiEvent

    data object OnResendVerificationClick : AuthUiEvent
    data object OnContinueClick : AuthUiEvent
}