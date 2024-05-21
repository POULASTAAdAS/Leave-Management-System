package com.poulastaa.lms.data.model.stoe_details

import kotlinx.serialization.Serializable

@Serializable
enum class AddressType(val id: Int) {
    PRESENT(1),
    HOME(2)
}