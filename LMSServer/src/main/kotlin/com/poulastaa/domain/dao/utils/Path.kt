package com.poulastaa.domain.dao.utils

import com.poulastaa.data.model.table.utils.PathTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Path(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Path>(PathTable)

    val type by PathTable.type

    enum class PathType(val value: String) {
        PRINCIPLE("Principal"),
        HEAD_CLARK("Head Clark"),
        DEPARTMENT_HEAD("Department Head")
    }
}