package com.poulastaa.data.model.table.utils

import com.poulastaa.data.model.table.utils.PrincipalTable.nullable
import org.jetbrains.exposed.dao.id.IntIdTable

object HeadClarkTable : IntIdTable() {
    val name = text("name")
    val email = varchar("email", 255).uniqueIndex()
    val profilePic = varchar("profilePic", 300).nullable()
}