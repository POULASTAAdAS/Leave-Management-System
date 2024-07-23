package com.poulastaa.lms.data.model.leave

import kotlinx.serialization.Serializable

@Serializable
data class ViewLeaveSingleRes(
    val department: String = "",
    val listOfLeave: List<ViewLeaveInfoRes> = emptyList(),
)

@Serializable
data class ViewLeaveInfoRes(
    val name: String = "",
    val leaveType: String = "",
    val reqData: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    val totalDays: String = "",
    val status: String = "",
    val cause: String = "",
)
