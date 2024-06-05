package com.poulastaa.lms.presentation.define_in_charge

import com.poulastaa.lms.presentation.store_details.ListHolder

data class DefneInChargeUiState(
    val isMakingApiCall: Boolean = false,
    val departments: ListHolder = ListHolder(
        all = listOf(
            "ASP(Advertisement and Sales Promotion)",
            "Bengali",
            "Botany",
            "Chemistry",
            "Commerce",
            "Computer Science",
            "Economics",
            "Education",
            "Electronic Science",
            "English",
            "Environmental Science",
            "Food & Nutrition",
            "Geography",
            "Hindi",
            "History",
            "Journalism & Mass Com.",
            "Mathematics",
            "Philosophy",
            "Physical Education",
            "Physics",
            "Physiology",
            "Sanskrit",
            "Sociology",
            "Urdu",
            "Zoology"
        )
    ),

    val isVisible: Boolean = false,
    val canReq: Boolean = false,

    val current: String = "Select Department",
    val others: TeacherHolder = TeacherHolder(),

    val isDefiningHead: Boolean = false,
)

data class TeacherHolder(
    val isDialogOpen: Boolean = false,
    val selected: String = "",
    val teachers: List<DepartmentTeacher> = emptyList(),
)

data class DepartmentTeacher(
    val id: Int = -1,
    val name: String = "",
)
