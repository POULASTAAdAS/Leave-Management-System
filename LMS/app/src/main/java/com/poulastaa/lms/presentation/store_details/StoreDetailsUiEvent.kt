package com.poulastaa.lms.presentation.store_details

sealed interface StoreDetailsUiEvent {
    data class OnUserNameChange(val userName: String) : StoreDetailsUiEvent
    data class OnHRMSIDChange(val id: String) : StoreDetailsUiEvent
    data class OnEmailChanged(val email: String) : StoreDetailsUiEvent
    data class OnPhoneNumberOneChange(val phoneNumber: String) : StoreDetailsUiEvent
    data class OnPhoneNumberTwoChange(val phoneNumber: String) : StoreDetailsUiEvent

    data object OnBDateDialogClick : StoreDetailsUiEvent
    data class OnBDateChange(val date: String) : StoreDetailsUiEvent

    data object OnGenderDropDownClick : StoreDetailsUiEvent
    data class OnGenderSelected(val index: Int) : StoreDetailsUiEvent

    data object OnDesignationDropDownClick : StoreDetailsUiEvent
    data class OnDesignationSelected(val index: Int) : StoreDetailsUiEvent

    data object OnDepartmentDropDownClick : StoreDetailsUiEvent
    data class OnDepartmentSelected(val index: Int) : StoreDetailsUiEvent

    data object OnJoinDateDialogClick : StoreDetailsUiEvent
    data class OnJoinDateChange(val date: String) : StoreDetailsUiEvent

    data object OnQualificationDropDownClick : StoreDetailsUiEvent
    data class OnQualificationSelected(val index: Int) : StoreDetailsUiEvent


    sealed interface PresentAddress : StoreDetailsUiEvent {
        data class HouseNumberChange(val data: String) : PresentAddress
        data class StreetChange(val data: String) : PresentAddress
        data class CityChange(val data: String) : PresentAddress
        data class ZipCodeChange(val data: String) : PresentAddress
        data class StateChange(val data: String) : PresentAddress
        data class CountryChange(val data: String) : PresentAddress
    }

    data object OnSameAsPresentAddressClick : StoreDetailsUiEvent

    sealed interface HomeAddress : StoreDetailsUiEvent {
        data class HouseNumberChange(val data: String) : HomeAddress
        data class StreetChange(val data: String) : HomeAddress
        data class CityChange(val data: String) : HomeAddress
        data class ZipCodeChange(val data: String) : HomeAddress
        data class StateChange(val data: String) : HomeAddress
        data class CountryChange(val data: String) : HomeAddress
    }

    data object OnContinueClick : StoreDetailsUiEvent
}