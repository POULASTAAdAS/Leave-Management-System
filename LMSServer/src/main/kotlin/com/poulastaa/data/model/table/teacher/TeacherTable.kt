package com.poulastaa.data.model.table.teacher

import org.jetbrains.exposed.dao.id.IntIdTable

object TeacherTable : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val emailVerified = bool("emailVerified").default(false)
}