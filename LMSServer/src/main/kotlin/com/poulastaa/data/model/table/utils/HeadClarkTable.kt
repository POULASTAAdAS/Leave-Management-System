package com.poulastaa.data.model.table.utils

import org.jetbrains.exposed.dao.id.IntIdTable

object HeadClarkTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
}