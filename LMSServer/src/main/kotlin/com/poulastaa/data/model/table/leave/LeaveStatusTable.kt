package com.poulastaa.data.model.table.leave

import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object LeaveStatusTable : Table() {
    val leaveId = reference("leaveId", LeaveReqTable.id, onDelete = ReferenceOption.CASCADE)
    val statusId = reference("statusId", StatusTable.id, onDelete = ReferenceOption.CASCADE)
    val pendingEndId = reference("pendingEndId", PendingEndTable.id, onDelete = ReferenceOption.CASCADE)
    val departmentId = reference("departmentId", DepartmentTable.id, onDelete = ReferenceOption.CASCADE)
    val cause = text("cause").default("")
    val actionId =
        reference("actionId", LeaveActionTable.id, onDelete = ReferenceOption.CASCADE).nullable().default(null)

    override val primaryKey = PrimaryKey(leaveId)
}