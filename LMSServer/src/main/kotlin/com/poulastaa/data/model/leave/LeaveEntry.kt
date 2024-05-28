package com.poulastaa.data.model.leave

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDate
import java.time.LocalDateTime

data class LeaveEntry(
    val teacherId: EntityID<Int>,
    val leaveTypeId: EntityID<Int>,
    val reqData: LocalDateTime,
    val toDate: LocalDate,
    val fromDate: LocalDate,
    val totalDays: Double,
    val reason: String,
    val addressDuringLeave: String,
    val pathId: EntityID<Int>,
    val doc: String? = null,
)
