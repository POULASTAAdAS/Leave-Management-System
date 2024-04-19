package online.poulastaa.data.model.table

import online.poulastaa.data.model.table.DesignationTable.uniqueIndex
import org.jetbrains.exposed.dao.id.IntIdTable

object QualificationTable : IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}