package com.poulastaa.lms.data.model.remove_employee

import kotlinx.serialization.Serializable

@Serializable
data class ResponseTeacher(
    val id: Int,
    val name: String,
    val designation: String,
    val profile: String,
)
