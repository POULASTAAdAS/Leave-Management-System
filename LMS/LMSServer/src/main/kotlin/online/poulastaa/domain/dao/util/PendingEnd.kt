package online.poulastaa.domain.dao.util

import online.poulastaa.data.model.table.util.PendingEndTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PendingEnd(id : EntityID<Int>): IntEntity(id)  {
    companion object: IntEntityClass<PendingEnd>(PendingEndTable)

    val type by PendingEndTable.type
}