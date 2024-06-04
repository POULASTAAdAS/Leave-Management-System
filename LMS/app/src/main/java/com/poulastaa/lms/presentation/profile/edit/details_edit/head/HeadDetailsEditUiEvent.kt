package com.poulastaa.lms.presentation.profile.edit.details_edit.head

sealed interface HeadDetailsEditUiEvent {
    data class OnNameChange(val name: String) : HeadDetailsEditUiEvent
    data class OnEmailChanged(val email: String) : HeadDetailsEditUiEvent

    data object OnSaveClick : HeadDetailsEditUiEvent
}