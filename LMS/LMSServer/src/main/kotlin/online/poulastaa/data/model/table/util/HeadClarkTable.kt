package online.poulastaa.data.model.table.util

import org.jetbrains.exposed.dao.id.IntIdTable

object HeadClarkTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
}