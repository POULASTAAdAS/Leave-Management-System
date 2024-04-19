package online.poulastaa.domain.dao.leave

import online.poulastaa.data.model.table.leave.LeaveTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveType(id : EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<LeaveType>(LeaveTypeTable)

    val type by LeaveTypeTable.type
}