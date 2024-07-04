package com.poulastaa.data.model.details

import com.poulastaa.data.model.constants.AddressType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAddressReq(
    val type: AddressType,
    val otherType: Boolean,

    val houseNo: String? = null,
    val street: String? = null,
    val zipCode: String? = null,
    val city: String? = null,
    val state: String? = null,
)
