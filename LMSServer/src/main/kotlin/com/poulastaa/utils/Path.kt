package com.poulastaa.utils

import com.poulastaa.data.model.table.utils.PathTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Path(id : EntityID<Int>): IntEntity(id)  {
    companion object: IntEntityClass<Path>(PathTable)

    val type by PathTable.type
}