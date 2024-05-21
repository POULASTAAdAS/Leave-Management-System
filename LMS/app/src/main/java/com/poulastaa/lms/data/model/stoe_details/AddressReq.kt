package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
data class AddressReq(
    val houseNumber: String,
    val street: String,
    val city: String,
    val zipcode: String,
    val state: String,
    val country: String
)