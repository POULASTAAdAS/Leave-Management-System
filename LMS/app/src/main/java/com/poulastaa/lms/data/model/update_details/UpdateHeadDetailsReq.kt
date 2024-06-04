package com.poulastaa.lms.data.model.update_details

import kotlinx.serialization.Serializable

@Serializable
data class UpdateHeadDetailsReq(
    val type: String,
    val email: String,
    val name: String
)
