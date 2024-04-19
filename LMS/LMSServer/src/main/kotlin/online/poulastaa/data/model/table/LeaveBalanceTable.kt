package online.poulastaa.data.model.table

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object LeaveBalanceTable: Table() {
    val teacherId = TeacherTable.reference("teacherId" , TeacherTable.id , onDelete = ReferenceOption.CASCADE)
    val teacherTypeId = TeacherTypeTable.reference("teacherTypeId" , TeacherTypeTable.id , onDelete = ReferenceOption.CASCADE)
    val leaveTypeId = LeaveTypeTable.reference("leaveTypeId" , LeaveTypeTable.id , onDelete = ReferenceOption.CASCADE)
    val leaveBalance = double("leaveBalance")
    val year = date("year").default(LocalDate.now())

    override val primaryKey = PrimaryKey(teacherId , teacherTypeId , leaveTypeId)
}






