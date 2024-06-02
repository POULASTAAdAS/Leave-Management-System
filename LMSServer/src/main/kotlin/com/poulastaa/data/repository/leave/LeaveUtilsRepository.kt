package com.poulastaa.data.repository.leave

import com.poulastaa.data.model.leave.LeaveHistoryRes
import com.poulastaa.domain.dao.leave.LeaveType
import org.jetbrains.exposed.dao.id.EntityID

interface LeaveUtilsRepository {
    suspend fun getLeaveBalance(
        teacherId: Int,
        type: String
    ): String?

    suspend fun getLeaveType(type: String): LeaveType

    suspend fun getLeaves(
        teacherId: Int,
        page: Int,
        pageSize: Int
    ): List<LeaveHistoryRes>

    suspend fun getPendingEndId(isPermanent: Boolean): EntityID<Int>
    suspend fun getPendingStatusId(): EntityID<Int>
}