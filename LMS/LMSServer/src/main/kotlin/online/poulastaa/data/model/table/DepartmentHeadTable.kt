package online.poulastaa.data.model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DepartmentHeadTable : IntIdTable() {
    val teacherId = reference("teacherId", TeacherTable.id, onDelete = ReferenceOption.CASCADE)
    val departmentHead = reference("departmentHead", DepartmentTable.id, onDelete = ReferenceOption.CASCADE)
}