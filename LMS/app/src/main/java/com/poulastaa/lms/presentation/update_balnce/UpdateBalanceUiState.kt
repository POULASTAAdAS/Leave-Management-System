package com.poulastaa.lms.presentation.update_balnce

import com.poulastaa.lms.data.model.leave.GetDepartmentTeacher
import com.poulastaa.lms.data.model.leave.TeacherLeaveBalance
import com.poulastaa.lms.presentation.store_details.ListHolder

data class UpdateBalanceUiState(
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
            "Zoology"
        )
    ),
    val isRequestingDepartment: Boolean = false,

    val teacher: ListHolder = ListHolder(),
    val isRequestingTeacher: Boolean = false,

    val mapOfLeave: Map<String, String> = emptyMap(),
    val listOfLeave: ListHolder = ListHolder(),
    val isRequestingLeave: Boolean = false,
    val leaveBalance: String = "0",

    val isMakingApiCall: Boolean = false,


    val responseTeacher: GetDepartmentTeacher = GetDepartmentTeacher(),
    val responseBalance: List<TeacherLeaveBalance> = emptyList(),
)
