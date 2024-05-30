package com.poulastaa.data.model.table.leave

import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.utils.PathTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object LeaveReqTable : LongIdTable() {
    val teacherId =reference("teacherId", TeacherTable.id, onDelete = ReferenceOption.CASCADE)
    val leaveTypeId = reference("leaveTypeId", LeaveTypeTable.id, onDelete = ReferenceOption.CASCADE)
    val reqDate = datetime("reqDate")
    val fromDate = date("fromDate")
    val toDate = date("toDate")
    val reason = text("reason").default("")
    val addressDuringLeave = varchar("addressDuringLeave", 100)
    val pathId = reference("pathId", PathTable.id, onDelete = ReferenceOption.CASCADE)
    val doc = varchar("doc" , 255).nullable().default(null)
}