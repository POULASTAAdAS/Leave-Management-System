package com.poulastaa.data.model.auth.req

import com.poulastaa.data.model.constants.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class SetDetailsReq(
    val email: String,
    val name: String,
    val hrmsId: String,
    val phone_1: String,
    val phone_2: String,
    val dbo: String,
    val sex: Char,
    val designation: String,
    val department: String,
    val joiningDate: String,
    val exp: String,
    val qualification: String,
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
