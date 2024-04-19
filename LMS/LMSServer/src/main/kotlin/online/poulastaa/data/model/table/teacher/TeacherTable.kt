package online.poulastaa.data.model.table.teacher

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TeacherTable : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val teacherTypeId = reference("teacherTypeId", TeacherTypeTable.id, onDelete = ReferenceOption.CASCADE)
}