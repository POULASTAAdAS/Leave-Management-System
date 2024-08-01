package com.poulastaa.data.model.other

import kotlinx.serialization.Serializable

@Serializable
data class ResponseTeacher(
    val id: Int,
    val name: String,
    val designation: String,
    val profile: String,
)