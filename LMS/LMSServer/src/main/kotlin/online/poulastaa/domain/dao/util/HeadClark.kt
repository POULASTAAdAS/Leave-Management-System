package online.poulastaa.domain.dao.util

import online.poulastaa.data.model.table.util.HeadClarkTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class HeadClark(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<HeadClark>(HeadClarkTable)

    val name by HeadClarkTable.name
    val email by HeadClarkTable.email
}