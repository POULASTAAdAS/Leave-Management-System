package com.poulastaa.lms.presentation.leave_view

import com.poulastaa.lms.presentation.store_details.ListHolder

data class LeaveViewUiState(
    val isHead: Boolean = false,
    val teacherDepartment: String = "",

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
            "Other"
        )
    ),
)
