package com.poulastaa.data.report

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val department: String? = null,
    val name: String,
    val listOfLeave: List<LeaveData>,
)

@Serializable
data class LeaveData(
    val id: Long,
    val applicationDate: String,
    val reqType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
)