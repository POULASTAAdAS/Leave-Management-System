package com.poulastaa.domain.dao.leave

import com.poulastaa.data.model.table.leave.LeaveTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LeaveType>(LeaveTypeTable)

    val type by LeaveTypeTable.type

    enum class TYPE {
        CASUAL_LEAVE,
        MEDICAL_LEAVE,
        STUDY_LEAVE,
        EARNED_LEAVE,
        ON_DUTY_LEAVE,
        SPECIAL_STUDY_LEAVE,
        MATERNITY_LEAVE,
        QUARANTINE_LEAVE,
        COMMUTED_LEAVE,
        EXTRAORDINARY_LEAVE,
        COMPENSATORY_LEAVE,
        LEAVE_NOT_DUE,
        SPECIAL_DISABILITY_LEAVE
    }
}