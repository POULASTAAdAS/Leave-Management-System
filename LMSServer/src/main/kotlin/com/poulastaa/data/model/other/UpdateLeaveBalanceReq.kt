package com.poulastaa.data.model.other

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLeaveBalanceReq(
    val teacherId: Int,
    val leaveId: Int,
    val value: String,
)
