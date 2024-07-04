package com.poulastaa.data.model.table.designation

import org.jetbrains.exposed.dao.id.IntIdTable

object DesignationTable : IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}