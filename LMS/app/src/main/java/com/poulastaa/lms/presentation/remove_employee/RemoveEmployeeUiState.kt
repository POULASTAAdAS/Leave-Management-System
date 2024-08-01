package com.poulastaa.lms.presentation.remove_employee

import com.poulastaa.lms.presentation.store_details.ListHolder

data class RemoveEmployeeUiState(
    val isMakingApiCall: Boolean = false,
    val cookie: String = "",
    val department: ListHolder = ListHolder(
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
            "Zoology",
            "NTS"
        )
    ),

    val teacher: List<UiTeacher> = emptyList(),
)

data class UiTeacher(
    val id: Int,
    val name: String,
    val designation: String,
    val profile: String,
)