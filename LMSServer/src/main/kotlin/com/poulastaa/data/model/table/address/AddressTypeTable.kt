package com.poulastaa.data.model.table.address

import org.jetbrains.exposed.dao.id.IntIdTable

object AddressTypeTable : IntIdTable() {
    val type = varchar("type", 400).uniqueIndex()
}