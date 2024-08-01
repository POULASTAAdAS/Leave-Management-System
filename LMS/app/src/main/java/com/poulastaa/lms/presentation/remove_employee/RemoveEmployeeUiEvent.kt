package com.poulastaa.lms.presentation.remove_employee

sealed interface RemoveEmployeeUiEvent {
    data object OnDepartmentToggle : RemoveEmployeeUiEvent
    data class OnDepartmentChange(val index: Int) : RemoveEmployeeUiEvent

    data class OnTeacherSelected(val id: Int) : RemoveEmployeeUiEvent
    data class OnConformClick(val id: Int) : RemoveEmployeeUiEvent
}