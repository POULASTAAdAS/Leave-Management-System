package com.poulastaa.data.repository.leave

import com.poulastaa.data.model.leave.LeaveApproveRes
import com.poulastaa.data.model.leave.LeaveHistoryRes
import com.poulastaa.data.model.leave.ViewLeaveSingleRes
import com.poulastaa.data.model.other.HeadType
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import org.jetbrains.exposed.dao.id.EntityID

interface LeaveUtilsRepository {
    suspend fun getLeaveBalance(
        teacherId: Int,
        type: String,
    ): String?

    suspend fun getLeaveType(type: String): LeaveType

    suspend fun getHistoryLeaves(
        teacherId: Int,
        page: Int,
        pageSize: Int,
    ): List<LeaveHistoryRes>

    suspend fun getPendingEndId(isPermanent: Boolean): EntityID<Int>
    suspend fun getPendingStatusId(): EntityID<Int>
    suspend fun getApproveLeaveAsDepartmentHead(
        departmentId: Int,
        teacherHeadId: Int,
        page: Int,
        pageSize: Int,
    ): List<LeaveApproveRes>

    suspend fun getApproveLeaveAsHead(
        page: Int,
        pageSize: Int,
        isPrincipal: Boolean,
    ): List<LeaveApproveRes>

    suspend fun getLeaveOnId(leaveId: Long): LeaveReq

    suspend fun viewLeave(
        dpId: Int,
        teacherId: Int,
        email: String,
        page: Int,
        pageSize: Int,
        headType: HeadType,
    ): List<ViewLeaveSingleRes>
}