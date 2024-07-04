package com.poulastaa.domain.dao.leave

import com.poulastaa.data.model.table.leave.LeaveReqTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveReq(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<LeaveReq>(LeaveReqTable)

    var teacherId by LeaveReqTable.teacherId
    var leaveTypeId by LeaveReqTable.leaveTypeId
    var reqDate by LeaveReqTable.reqDate
    var toDate by LeaveReqTable.toDate
    var fromDate by LeaveReqTable.fromDate
    var reason by LeaveReqTable.reason
    var addressDuringLeave by LeaveReqTable.addressDuringLeave
    var pathId by LeaveReqTable.pathId
    var doc by LeaveReqTable.doc
}