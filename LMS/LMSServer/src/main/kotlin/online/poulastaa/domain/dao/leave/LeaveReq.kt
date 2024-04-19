package online.poulastaa.domain.dao.leave

import online.poulastaa.data.model.table.leave.LeaveReqTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveReq(id : EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<LeaveReq>(LeaveReqTable)

    val teacherId by LeaveReqTable.teacherId
    val leaveTypeId by LeaveReqTable.leaveTypeId
    val reqDate by LeaveReqTable.reqDate
    val toDate by LeaveReqTable.toDate
    val fromDate by LeaveReqTable.fromDate
    val reason by LeaveReqTable.reason
    val addressDuringLeave by LeaveReqTable.addressDuringLeave
    val pathId by LeaveReqTable.pathId
    val doc by LeaveReqTable.doc
}