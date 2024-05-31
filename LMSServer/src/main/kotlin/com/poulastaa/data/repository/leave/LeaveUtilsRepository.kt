package com.poulastaa.data.repository.leave

import com.poulastaa.domain.dao.leave.LeaveType

interface LeaveUtilsRepository {
    suspend fun getLeaveBalance(
        teacherId: Int,
        type: String
    ): String?

    suspend fun getLeaveType(type: String): LeaveType
}