package online.poulastaa.data.model.table.leave

import org.jetbrains.exposed.dao.id.IntIdTable

object LeaveTypeTable: IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}