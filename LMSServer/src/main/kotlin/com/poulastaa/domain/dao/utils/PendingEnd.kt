package com.poulastaa.domain.dao.utils

import com.poulastaa.data.model.table.utils.PendingEndTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PendingEnd(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PendingEnd>(PendingEndTable)

    val type by PendingEndTable.type

    enum class TYPE(val value: String) {
        DEPARTMENT_LEVEL("Department Level"),
        PRINCIPLE_LEVEL("Principal Level"),
        NOT_PENDING("Not Pending")
    }
}