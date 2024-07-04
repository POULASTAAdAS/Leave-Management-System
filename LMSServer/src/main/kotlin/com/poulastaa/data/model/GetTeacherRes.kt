package com.poulastaa.data.model

import com.poulastaa.data.model.auth.req.ReqAddress
import com.poulastaa.data.model.constants.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class GetTeacherRes(
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

    val address: List<Pair<AddressType, ReqAddress>> = emptyList(),
)