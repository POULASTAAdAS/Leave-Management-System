package com.poulastaa.data.model.table.utils

import org.jetbrains.exposed.dao.id.IntIdTable

object PrincipalTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
    val profilePic = varchar("profilePic", 300).nullable()
}