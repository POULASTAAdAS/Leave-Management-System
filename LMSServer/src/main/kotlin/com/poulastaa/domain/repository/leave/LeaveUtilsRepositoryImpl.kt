package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.table.leave.LeaveBalanceTable
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.plugins.dbQuery
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.upperCase

class LeaveUtilsRepositoryImpl : LeaveUtilsRepository {
    private suspend fun getLeaveType(type: String) = dbQuery {
        LeaveType.find {
            LeaveTypeTable.type.upperCase() eq type.uppercase()
        }.singleOrNull()
    }

    override suspend fun getLeaveBalance(
        teacherId: Int,
        type: String
    ): String? = dbQuery {
        val entry = getLeaveType(type) ?: return@dbQuery null

        LeaveBalanceTable.select {
            LeaveBalanceTable.teacherId eq teacherId and (LeaveBalanceTable.leaveTypeId eq entry.id)
        }.singleOrNull()?.let {
            it[LeaveBalanceTable.leaveBalance].toString()
        }
    }
}