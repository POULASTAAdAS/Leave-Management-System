package com.poulastaa.lms.data.model.departmnet_in_charge

import kotlinx.serialization.Serializable

@Serializable
data class GetDepartmentInChargeRes(
    val current: String = "",
    val others: List<DepartmentTeacherRes> = emptyList(),
)

@Serializable
data class DepartmentTeacherRes(
    val id: Int = -1,
    val name: String = "",
)