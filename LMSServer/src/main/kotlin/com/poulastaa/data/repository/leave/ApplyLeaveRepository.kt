package com.poulastaa.data.repository.leave

import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.data.model.leave.HandleLeaveReq
import com.poulastaa.domain.dao.leave.LeaveAction

interface ApplyLeaveRepository {
    suspend fun applyLeave(
        req: ApplyLeaveReq,
        doc: String?
    ): ApplyLeaveRes

    suspend fun handleLeave(
        req: HandleLeaveReq,
        isPrincipal: Boolean
    ): Pair<LeaveAction.TYPE , Int>
}