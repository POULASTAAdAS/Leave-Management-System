package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
data class UnAuth(
    val message: String = "Unauthorised",
)
