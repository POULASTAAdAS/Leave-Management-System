package online.poulastaa.data.model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object TeacherTable : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val teacherTypeId = reference("teacherTypeId", TeacherTypeTable.type, onDelete = ReferenceOption.CASCADE)
}