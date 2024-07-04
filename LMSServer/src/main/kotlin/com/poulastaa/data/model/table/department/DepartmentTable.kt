package com.poulastaa.data.model.table.department

import org.jetbrains.exposed.dao.id.IntIdTable

object DepartmentTable : IntIdTable() {
    val name = varchar("name", 200).uniqueIndex()
}