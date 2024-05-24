package com.poulastaa.lms.presentation.profile

data class ProfileUiState(
    val isInternet: Boolean = false,
    val isMakingApiCall: Boolean = true,

    val isProfilePicUpdating: Boolean = false,

    val name: String = "",
    val profilePicUrl: String = "",
    val gender: String = "",
    val personalDetails: PersonalDetails = PersonalDetails(),
    val otherDetails: OtherDetails = OtherDetails(),
    val homeAddress: ProfileUiAddress = ProfileUiAddress(),
    val presentAddress: ProfileUiAddress = ProfileUiAddress(),
)


data class PersonalDetails(
    val email: String = "",
    val phoneOne: String = "",
    val phoneTwo: String = "",
    val qualification: String = "",
)

data class OtherDetails(
    val department: String = "",
    val exp: String = "",
    val joiningDate: String = "",
)

data class ProfileUiAddress(
    val houseNumber: String = "",
    val street: String = "",
    val city: String = "",
    val zipcode: String = "",
    val state: String = "",
    val country: String = ""
)