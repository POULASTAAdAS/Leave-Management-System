package com.poulastaa.lms.presentation.leave_view

sealed interface LeaveViewUiEvent {
    data class OnLeaveToggle(val department: String) : LeaveViewUiEvent
    data object OnDepartmentToggle : LeaveViewUiEvent
    data class OnDepartmentChange(val index: Int) : LeaveViewUiEvent
}