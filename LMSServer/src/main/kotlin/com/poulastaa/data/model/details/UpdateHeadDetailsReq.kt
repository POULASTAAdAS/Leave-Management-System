package com.poulastaa.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class UpdateHeadDetailsReq(
    val type: String,
    val email: String,
    val name: String,
)
