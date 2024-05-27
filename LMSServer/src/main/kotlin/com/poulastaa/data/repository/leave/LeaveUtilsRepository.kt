package com.poulastaa.data.repository.leave

interface LeaveUtilsRepository {
    suspend fun getLeaveBalance(
        teacherId: Int,
        type: String
    ): String?
}