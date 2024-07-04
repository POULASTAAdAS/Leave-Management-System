package com.poulastaa.data.model.department

import kotlinx.serialization.Serializable

@Serializable
data class GetDepartmentInChargeRes(
    val current: String = "",
    val others: List<DepartmentTeacher> = emptyList(),
)

@Serializable
data class DepartmentTeacher(
    val id: Int = -1,
    val name: String = "",
)
