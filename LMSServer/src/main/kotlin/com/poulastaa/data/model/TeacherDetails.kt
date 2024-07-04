package com.poulastaa.data.model

data class TeacherDetails(
    val name: String,
    val email: String,
    val teacherTypeId: Int,
    val profileImage: String? = null,
    val phoneOne: String,
    val phoneTwo: String? = null,
    val designationId: Int,
    val departmentId: Int,
    val joiningDate: String,
    val dob: String,
    val exp: String,
    val gender: String,
)
