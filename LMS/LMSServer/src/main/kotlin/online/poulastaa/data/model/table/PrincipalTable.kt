package online.poulastaa.data.model.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object PrincipalTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
}