package com.poulastaa.lms.presentation.update_balnce

sealed interface UpdateBalanceUiEvent {
    data object OnDepartmentToggle : UpdateBalanceUiEvent
    data class OnDepartmentSelected(val index: Int) : UpdateBalanceUiEvent

    data object OnTeacherToggle : UpdateBalanceUiEvent
    data class OnTeacherSelected(val index: Int) : UpdateBalanceUiEvent

    data object OnLeaveToggle : UpdateBalanceUiEvent
    data class OnLeaveSelected(val index: Int) : UpdateBalanceUiEvent

    data class OnLeaveBalanceChange(val value: String) : UpdateBalanceUiEvent
    data object OnContinueClick : UpdateBalanceUiEvent
}