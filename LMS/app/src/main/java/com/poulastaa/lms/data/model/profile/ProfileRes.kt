package com.poulastaa.lms.data.model.profile

import com.poulastaa.lms.data.model.stoe_details.AddressReq
import com.poulastaa.lms.data.model.stoe_details.AddressType

data class ProfileRes(
    val name: String = "",
    val profilePicUrl: String = "",
    val gender: String = "",
    val email: String = "",
    val phoneOne: String = "",
    val phoneTwo: String = "",
    val qualification: String = "",
    val department: String = "",
    val exp: String = "",
    val joiningDate: String = "",
    val designation: String = "",

    val address: List<Pair<AddressType, AddressReq>> = emptyList()
)
