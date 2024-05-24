package com.poulastaa.data.model.convertors

import org.jetbrains.exposed.sql.statements.api.ExposedBlob

data class TeacherProfileEntry(
    val id: Int,
    val name: String,
    val profilePic: ExposedBlob
)
