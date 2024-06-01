package com.poulastaa.lms.presentation.leave_status

sealed interface LeaveStatusUiEvent {
    data object OnLeaveTypeToggle: LeaveStatusUiEvent
    data class OnLeaveTypeSelected(val index: Int) : LeaveStatusUiEvent
}