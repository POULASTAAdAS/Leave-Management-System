package com.poulastaa.domain.dao.leave

import com.poulastaa.data.model.table.leave.LeaveTypeTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class LeaveType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LeaveType>(LeaveTypeTable)

    val type by LeaveTypeTable.type

    enum class ScatType(val value: String) {
        CASUAL_LEAVE("Casual Leave"),
        MEDICAL_LEAVE("Medical Leave"),
        ON_DUTY_LEAVE("On Duty Leave"),
        MATERNITY_LEAVE("Maternity Leave"),
        QUARANTINE_LEAVE("Quarintine Leave")
    }

    enum class PermanentType(val value: String) {
        CASUAL_LEAVE("Casual Leave"),
        MEDICAL_LEAVE("Medical Leave"),
        STUDY_LEAVE("Study Leave"),
        ON_DUTY_LEAVE("On Duty Leave"),
        EARNED_LEAVE("Earned Leave"),
        SPECIAL_STUDY_LEAVE("Special Study Leave"),
        MATERNITY_LEAVE("Maternity Leave"),
        QUARANTINE_LEAVE("Quarintine Leave"),
        COMMUTED_LEAVE("Commuted Leave"),
        EXTRAORDINARY_LEAVE("Extraordinary Leave"),
        COMPENSATORY_LEAVE("Compensatory Leave"),
        LEAVE_NOT_DUE("Leave Not Due"),
        SPECIAL_DISABILITY_LEAVE("Special Disability Leave")
    }
}