package com.poulastaa.lms.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class GetDepartmentTeacher(
    val departmentId: Int = -1,
    val teacherName: List<Teacher> = emptyList(),
)

@Serializable
data class Teacher(
    val id: Int,
    val name: String,
)
