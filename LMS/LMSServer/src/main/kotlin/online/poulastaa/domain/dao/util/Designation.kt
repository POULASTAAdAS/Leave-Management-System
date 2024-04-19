package online.poulastaa.domain.dao.util

import online.poulastaa.data.model.table.util.DesignationTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Designation(id : EntityID<Int>): IntEntity(id)  {
    companion object : IntEntityClass<Designation>(DesignationTable)

    val type by DesignationTable.type
}