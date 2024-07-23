package com.poulastaa.data.model.other

import kotlinx.serialization.Serializable

@Serializable
data class TeacherLeaveBalance(
    val id: Int,
    val name: String,
    val balance: String,
)
