package com.poulastaa.data.model.constants

import kotlinx.serialization.Serializable

@Serializable
enum class AddressType(val id: Int) {
    PRESENT(1),
    HOME(2)
}