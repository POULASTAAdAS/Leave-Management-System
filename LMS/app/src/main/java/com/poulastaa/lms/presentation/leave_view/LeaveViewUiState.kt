package com.poulastaa.lms.presentation.leave_view

import com.poulastaa.lms.presentation.store_details.ListHolder

data class LeaveViewUiState(
    val isMakingApiCall: Boolean = false,

    val teacherDepartment: String = "",

    val department: ListHolder = ListHolder(
        all = listOf(
            "All"
        ),
        selected = "All"
    ),
)
