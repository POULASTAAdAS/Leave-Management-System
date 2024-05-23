package com.poulastaa.domain.dao.address

import com.poulastaa.data.model.table.address.AddressTypeTable
import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AddressType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AddressType>(AddressTypeTable)

    val type by AddressTypeTable.type
}