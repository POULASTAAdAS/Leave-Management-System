package com.poulastaa.lms.presentation.profile.edit.address_edit

sealed interface AddressEditUiEvent {
    data class OnHouseNumberChang(val value: String) : AddressEditUiEvent
    data class OnStreetChang(val value: String) : AddressEditUiEvent
    data class OnCityChang(val value: String) : AddressEditUiEvent
    data class OnPostalCodeChang(val value: String) : AddressEditUiEvent
    data class OnStateChang(val value: String) : AddressEditUiEvent

    data object SomethingWentWrong : AddressEditUiEvent

    data object OtherAddressSelected : AddressEditUiEvent

    data object OnSaveClick : AddressEditUiEvent
}