package online.poulastaa.data.model.table.department

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object DepartmentTable : IntIdTable() {
    val name = varchar("name", 200).uniqueIndex()
}