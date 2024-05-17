package com.poulastaa.data.model.auth.req

import com.poulastaa.data.model.constants.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class SetDetailsReq(
    val email: String = "",
    val name: String = "",
    val htmsId: Long = -1,
    val phone_1: Long = -1,
    val phone_2: Long = -1,
    val dbo: String = "",
    val sex: Char = ' ',
    val designationId: Int = -1,
    val departmentId: Int = -1,
    val joiningDate: String = "",
    val exp: String = "",
    val qualificationId: Int = -1,
    val address: List<Pair<AddressType, Address>> = emptyList()
)

@Serializable
data class Address(
    val houseNumber: String = "",
    val street: String = "",
    val city: String = "kolkata",
    val zipcode: String = "",
    val state: String = "West Bengal",
    val country: String = "India",
)
