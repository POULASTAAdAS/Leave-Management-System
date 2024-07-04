package com.poulastaa.lms.ui.utils

import com.poulastaa.lms.data.model.auth.LocalUser
import com.poulastaa.lms.data.model.auth.ResponseUser
import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.data.model.stoe_details.AddressReq
import com.poulastaa.lms.data.model.stoe_details.AddressType
import com.poulastaa.lms.data.model.stoe_details.StoreDetailsReq
import com.poulastaa.lms.presentation.store_details.HolderAddress
import com.poulastaa.lms.presentation.store_details.StoreDetailsUiState

fun StoreDetailsUiState.toStoreDetailsReq() = StoreDetailsReq(
    email = this.email.data.trim(),
    name = this.userName.data.trim(),
    hrmsId = this.hrmsId.data.trim(),
    phone_1 = this.phoneOne.data.trim(),
    phone_2 = this.phoneTwo.data.trim(),
    dbo = this.bDay.data.trim().replace("/", "."),
    sex = this.gender.selected.trim()[0],
    designation = this.designation.selected.trim(),
    department = this.department.selected.trim(),
    joiningDate = this.joiningDate.data.trim().replace("/", "."),
    exp = this.experience.trim(),
    qualification = this.qualification.selected.trim(),
    address = listOf(
        Pair(
            first = AddressType.PRESENT,
            second = this.presentAddress.toAddressReq()
        ),
        Pair(
            first = AddressType.HOME,
            second = this.homeAddress.toAddressReq()
        )
    )
)

fun HolderAddress.toAddressReq() = AddressReq(
    houseNumber = this.houseNumber.data.trim(),
    street = this.street.data.trim(),
    city = this.city.data.trim(),
    zipcode = this.zipCode.data.trim(),
    state = this.state.data.trim(),
    country = this.country.data.trim()
)

fun ResponseUser.toLocalUser() = LocalUser(
    name = this.name,
    email = this.email,
    phone = this.phone,
    profilePicUrl = this.profilePicUrl,
    designation = this.designation,
    department = this.department,
    isDepartmentInCharge = this.isDepartmentInCharge,
    userType = if (this.designation.startsWith("S")) UserType.SACT
    else UserType.PERMANENT
)
