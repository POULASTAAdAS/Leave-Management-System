package com.poulastaa.lms.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class EmailVerificationRes(
    val status: Boolean
)
