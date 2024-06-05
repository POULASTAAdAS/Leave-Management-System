package com.poulastaa.lms.presentation.leave_view

sealed interface LeaveViewUiEvent {
    data class DepartmentToggle(val department: String) : LeaveViewUiEvent
}