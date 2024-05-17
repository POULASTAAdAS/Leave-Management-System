package com.poulastaa.utils

import com.poulastaa.data.model.table.utils.HeadClarkTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HeadClark(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HeadClark>(HeadClarkTable)

    val name by HeadClarkTable.name
    val email by HeadClarkTable.email
}