package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAddressReq(
    val type: AddressType,
    val otherType: Boolean,

    val houseNo: String,
    val street: String,
    val zipCode: String,
    val city: String,
    val state: String
)
