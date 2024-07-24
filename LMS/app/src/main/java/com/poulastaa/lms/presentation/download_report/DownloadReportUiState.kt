package com.poulastaa.lms.presentation.download_report

import com.poulastaa.lms.data.model.home.UserType
import com.poulastaa.lms.presentation.store_details.ListHolder
import kotlinx.serialization.Serializable

data class DownloadReportUiState(
    val isMakingApiCall: Boolean = false,

    val isDepartmentHead: Boolean = false,
    val userTpe: UserType = UserType.LOAD,
    val department: ListHolder = ListHolder(
        all = listOf(
            "All",
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
    val leaveType: ListHolder = ListHolder(
        all = listOf(
            "All",
            "Casual Leave(CL)",
            "Commuted Leave(CL)",
            "Compensatory Leave(CL)",
            "Earned Leave(EL)",
            "Extraordinary Leave(EL)",
            "Leave Not Due(LND)",
            "Maternity Leave(ML)",
            "Medical Leave(ML)",
            "On Duty Leave(OD)",
            "Quarintine Leave(QL)",
            "Special Disability Leave(SDL)",
            "Special Study Leave(SSL)",
            "Study Leave(SL)"
        )
    ),

    val prevResponse: List<ReportUiState> = emptyList(),
)

@Serializable
data class ReportUiState(
    val department: String? = null,
    val name: String,
    val listOfLeave: List<LeaveData>,
)

@Serializable
data class LeaveData(
    val id: Pair<String, String>,
    val applicationDate: Pair<String, String>,
    val reqType: Pair<String, String>,
    val fromDate: Pair<String, String>,
    val toDate: Pair<String, String>,
    val totalDays: Pair<String, String>,
)