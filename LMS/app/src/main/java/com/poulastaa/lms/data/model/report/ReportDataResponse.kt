package com.poulastaa.lms.data.model.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportDataResponse(
    val department: String? = null,
    val name: String,
    val listOfLeave: List<LeaveDataResponse>,
)

@Serializable
data class LeaveDataResponse(
    val id: Long,
    val applicationDate: String,
    val reqType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
)