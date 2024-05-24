package com.poulastaa.data.model.details

import com.poulastaa.data.model.constants.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAddressReq(
    val type: AddressType,

    val houseNo: String,
    val street: String,
    val zipCode: String,
    val city: String
)
