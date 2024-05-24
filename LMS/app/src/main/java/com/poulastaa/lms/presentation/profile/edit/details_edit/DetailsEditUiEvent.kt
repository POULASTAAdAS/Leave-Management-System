package com.poulastaa.lms.presentation.profile.edit.details_edit

sealed interface DetailsEditUiEvent {
    data class OnNameChange(val name: String) : DetailsEditUiEvent
    data class OnEmailChanged(val email: String) : DetailsEditUiEvent
    data class OnPhoneNumberOneChange(val phoneNumber: String) : DetailsEditUiEvent
    data class OnPhoneNumberTwoChange(val phoneNumber: String) : DetailsEditUiEvent

    data object OnQualificationDropDownClick : DetailsEditUiEvent
    data class OnQualificationSelected(val index: Int) : DetailsEditUiEvent

    data object OnSaveClick : DetailsEditUiEvent
    data object SomethingWentWrong : DetailsEditUiEvent
}