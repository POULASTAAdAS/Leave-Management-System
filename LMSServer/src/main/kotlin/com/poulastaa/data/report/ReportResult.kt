package com.poulastaa.data.report

data class ReportResult(
    val leaveId: Long,
    val departmentName: String,
    val teacherName: String,
    val leaveType: String,
    val reqDate: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: Int,
    val reason: String,
)
