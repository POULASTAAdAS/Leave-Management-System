package com.poulastaa.data.model.table.utils

import org.jetbrains.exposed.dao.id.LongIdTable

object LogInEmailTable : LongIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val emailVerified = bool("emailVerified").default(false)
}