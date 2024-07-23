package com.poulastaa.lms.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class TeacherLeaveBalance(
    val id: Int,
    val name: String,
    val balance: String,
)
