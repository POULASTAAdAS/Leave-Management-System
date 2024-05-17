package com.poulastaa.data.model.table.utils

import org.jetbrains.exposed.dao.id.IntIdTable

object PathTable : IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}