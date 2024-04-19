package online.poulastaa.domain.dao.leave

import online.poulastaa.data.model.table.leave.LeaveActionTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveAction(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<LeaveAction>(LeaveActionTable)

    val type by LeaveActionTable.type
}