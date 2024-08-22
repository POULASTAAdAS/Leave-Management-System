package com.poulastaa.data.model.other

data class PdfData(
    val department: String,
    val listOfData: List<PdfTeacher> = emptyList(),
)

data class PdfTeacher(
    val name: String,
    val designation: String,
    val listOfData: List<PdfReportData>,
)

data class PdfReportData(
    val applicationDate: String,
    val leaveType: String,
    val fromDate: String,
    val toDate: String,
    val totalDays: String,
    val status: String,
)
