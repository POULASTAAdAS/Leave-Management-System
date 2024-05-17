package com.poulastaa.utils

import com.poulastaa.data.model.table.utils.StatusTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Status(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Status>(StatusTable)

    val type by StatusTable.type
}