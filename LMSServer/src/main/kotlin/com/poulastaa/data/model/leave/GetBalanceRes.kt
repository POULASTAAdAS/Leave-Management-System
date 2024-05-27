package com.poulastaa.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class GetBalanceRes(
    val balance: String = "-1"
)
