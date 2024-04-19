package online.poulastaa.data.model.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object LeaveReqTable : LongIdTable() {
    val teacherId = TeacherTable.reference("teacherId", TeacherTable.id, onDelete = ReferenceOption.CASCADE)
    val leaveTypeId = LeaveTypeTable.reference("leaveTypeId", LeaveTypeTable.id, onDelete = ReferenceOption.CASCADE)
    val reqDate = datetime("reqDate")
    val fromDate = date("fromDate")
    val toDate = date("toDate")
    val reason = text("reason").default("")
    val addressDuringLeave = varchar("addressDuringLeave", 100)
    val pathId = PathTable.reference("pathId", PathTable.id, onDelete = ReferenceOption.CASCADE)
    val doc = blob("doc").nullable()
}