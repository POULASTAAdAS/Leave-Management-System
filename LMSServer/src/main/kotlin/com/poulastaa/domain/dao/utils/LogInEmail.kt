package com.poulastaa.domain.dao.utils

import com.poulastaa.data.model.table.utils.LogInEmailTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LogInEmail(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LogInEmail>(LogInEmailTable)

    var email by LogInEmailTable.email
    var emailVerified by LogInEmailTable.emailVerified
}