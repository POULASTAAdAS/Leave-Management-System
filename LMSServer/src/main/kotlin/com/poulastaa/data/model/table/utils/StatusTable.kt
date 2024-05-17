package com.poulastaa.data.model.table.utils

import org.jetbrains.exposed.dao.id.IntIdTable

object StatusTable: IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}