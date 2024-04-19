package online.poulastaa.domain.dao.util

import online.poulastaa.data.model.table.util.QualificationTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Qualification(id : EntityID<Int>): IntEntity(id)  {
    companion object : IntEntityClass<Qualification>(QualificationTable)

    val type by QualificationTable.type
}