package com.poulastaa.data.model.table.leave

import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LeaveStatusTable : Table() {
    val leaveId = LeaveReqTable.reference("leaveId", LeaveReqTable.id, onDelete = ReferenceOption.CASCADE)
    val statusId = StatusTable.reference("statusId", StatusTable.id, onDelete = ReferenceOption.CASCADE)
    val pendingEndId = PendingEndTable.reference("pendingEndId", PendingEndTable.id, onDelete = ReferenceOption.CASCADE)
    val cause = text("cause").default("")
    val actionId = LeaveActionTable.reference("actionId", LeaveActionTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(leaveId)
}