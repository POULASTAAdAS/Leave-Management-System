package com.poulastaa.data.repository.leave

data class LeaveWrapper(
    val applyLeave: ApplyLeaveRepository,
    val leaveUtils: LeaveUtilsRepository,
)
