package online.poulastaa.data.model.table

import online.poulastaa.data.model.table.TeacherTypeTable.uniqueIndex
import org.jetbrains.exposed.dao.id.IntIdTable

object PendingEndTable : IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}