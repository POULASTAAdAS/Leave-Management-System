package com.poulastaa.lms.presentation.add_teacher

sealed interface AddTeacherUiEvent {
    data class OnEmailChange(val value: String) : AddTeacherUiEvent
    data object OnConformClick : AddTeacherUiEvent
}