package com.poulastaa.lms.presentation.profile

import com.poulastaa.lms.BuildConfig
import com.poulastaa.lms.data.model.auth.EndPoints

data class ProfileUiState(
    val isInternet: Boolean = false,
    val isMakingApiCall: Boolean = true,

    val isProfilePicUpdating: Boolean = false,

    val cookie: String = "",

    val name: String = "",
    val profilePicUrl: String = BuildConfig.BASE_URL + EndPoints.GetProfilePic.route,
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