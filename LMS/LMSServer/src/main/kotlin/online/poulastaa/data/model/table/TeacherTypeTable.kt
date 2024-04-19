package online.poulastaa.data.model.table

import org.jetbrains.exposed.dao.id.IntIdTable

object TeacherTypeTable: IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}