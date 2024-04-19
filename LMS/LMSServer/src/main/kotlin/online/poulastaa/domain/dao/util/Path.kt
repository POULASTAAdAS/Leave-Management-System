package online.poulastaa.domain.dao.util

import online.poulastaa.data.model.table.util.PathTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Path(id : EntityID<Int>): IntEntity(id)  {
    companion object: IntEntityClass<Path>(PathTable)

    val type by PathTable.type
}