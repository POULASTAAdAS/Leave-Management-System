package com.poulastaa.lms.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class UpdateLeaveBalanceReq(
    val teacherId: Int,
    val leaveId: Int,
    val value: String,
)