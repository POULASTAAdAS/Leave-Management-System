package com.poulastaa.lms.presentation.profile.edit.address_edit

import com.poulastaa.lms.data.model.stoe_details.AddressType
import com.poulastaa.lms.presentation.store_details.Holder

data class AddressEditUiState(
    val isInternet: Boolean = false,

    val isMakingApiCall: Boolean = false,

    val type: AddressType = AddressType.HOME,

    val otherSelected: Boolean = false,

    val houseNumber: Holder = Holder(),
    val street: Holder = Holder(),
    val city: Holder = Holder(),
    val state: Holder = Holder(),
    val pinCode: Holder = Holder()
)
