package com.poulastaa.lms.presentation.define_in_charge

sealed interface DefneInChargeUiEvent {
    data object OnDepartmentToggle : DefneInChargeUiEvent
    data class OnDepartmentSelect(val index: Int) : DefneInChargeUiEvent

    data object OnTeacherToggle : DefneInChargeUiEvent
    data class OnTeacherSelect(val index: Int) : DefneInChargeUiEvent

    data object OnConformClick : DefneInChargeUiEvent
}