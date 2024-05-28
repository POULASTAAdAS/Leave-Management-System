package com.poulastaa.data.repository.leave

import com.poulastaa.data.model.TeacherDetails
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.domain.dao.teacher.Teacher

interface ApplyLeaveRepository {
    suspend fun applyLeave(
        req: ApplyLeaveReq,
        doc: String?
    ): ApplyLeaveRes
}