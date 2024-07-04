package com.poulastaa.data.model.table.leave

import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import java.time.LocalDate

object LeaveBalanceTable : Table() {
    val teacherId = reference("teacherId", TeacherTable.id, onDelete = ReferenceOption.CASCADE)
    val teacherTypeId = reference("teacherTypeId", TeacherTypeTable.id, onDelete = ReferenceOption.CASCADE)
    val leaveTypeId = reference("leaveTypeId", LeaveTypeTable.id, onDelete = ReferenceOption.CASCADE)
    val leaveBalance = double("leaveBalance")
    val year = integer("year").default(LocalDate.now().year)

    override val primaryKey = PrimaryKey(teacherId, teacherTypeId, leaveTypeId)
}