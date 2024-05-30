package com.poulastaa.data.repository.leave

import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes

interface ApplyLeaveRepository {
    suspend fun applyLeave(
        req: ApplyLeaveReq,
        doc: String?
    ): ApplyLeaveRes
}