package online.poulastaa.data.model.table.teacher

import org.jetbrains.exposed.dao.id.IntIdTable

object TeacherTypeTable: IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}