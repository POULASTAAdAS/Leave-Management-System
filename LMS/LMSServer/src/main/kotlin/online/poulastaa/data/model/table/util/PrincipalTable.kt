package online.poulastaa.data.model.table.util

import org.jetbrains.exposed.dao.id.IntIdTable

object PrincipalTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
}