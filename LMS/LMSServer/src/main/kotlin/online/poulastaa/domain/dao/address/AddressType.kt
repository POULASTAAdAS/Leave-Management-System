package online.poulastaa.domain.dao.address

import online.poulastaa.data.model.table.address.AddressTypeTable
import online.poulastaa.data.model.table.teacher.TeacherTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AddressType(id :EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AddressType>(AddressTypeTable)

    val type by TeacherTypeTable.type
}