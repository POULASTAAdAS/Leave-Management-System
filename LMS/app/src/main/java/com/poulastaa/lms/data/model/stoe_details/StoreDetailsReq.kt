package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
data class StoreDetailsReq(
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
    val address: List<Pair<AddressType, AddressReq>>
)
