package com.poulastaa.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDetailsReq(
    val name: String,
    val email: String,
    val phoneOne: String,
    val phoneTwo: String,
    val qualification: String,
)
