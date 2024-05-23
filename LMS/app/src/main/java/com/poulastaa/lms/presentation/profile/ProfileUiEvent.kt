package com.poulastaa.lms.presentation.profile

sealed interface ProfileUiEvent {
    data object OnProfileEditClick : ProfileUiEvent
    data object DetailsEditClick : ProfileUiEvent

    data object OnPresentAddressEditClick : ProfileUiEvent
    data object OnHomeAddressEditClick : ProfileUiEvent
}