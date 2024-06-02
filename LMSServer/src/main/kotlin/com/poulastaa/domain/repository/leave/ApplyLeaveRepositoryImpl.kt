package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.data.model.leave.ApplyLeaveStatus
import com.poulastaa.data.model.leave.LeaveEntry
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.leave.LeaveBalanceTable
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.utils.PathTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.leave.ApplyLeaveRepository
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.utils.Path
import com.poulastaa.plugins.dbQuery
import com.poulastaa.utils.toTeacherDetails
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ApplyLeaveRepositoryImpl(
    private val teacher: TeacherRepository,
    private val leaveUtils: LeaveUtilsRepository
) : ApplyLeaveRepository {
    override suspend fun applyLeave(
        req: ApplyLeaveReq,
        doc: String?
    ): ApplyLeaveRes {
        val teacher = teacher.getTeacher(req.email) ?: return ApplyLeaveRes()
        val teacherDetails = this.teacher.getTeacherDetails(req.email, teacher.id.value) ?: return ApplyLeaveRes()
        val teacherType = this.teacher.getTeacherTypeOnId(teacherDetails.teacherTypeId) ?: return ApplyLeaveRes()

        val path = dbQuery {
            Path.find {
                PathTable.type eq req.path
            }.singleOrNull()
        } ?: return ApplyLeaveRes()

        return when (teacherType) {
            TeacherType.SACT -> {
                val type = LeaveType.ScatType.valueOf(req.leaveType.uppercase().replace(' ', '_'))

                when (type) {
                    LeaveType.ScatType.CASUAL_LEAVE -> {
                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.CASUAL_LEAVE.value
                        )

                        leaveType to applyCasualLeaveForSACTeacher(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.ScatType.MEDICAL_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.MEDICAL_LEAVE.value
                        )

                        leaveType to applyMedicalLeaveForSACTeacher(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.ScatType.STUDY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.STUDY_LEAVE.value
                        )

                        leaveType to applyStudyLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }
                }
            }

            TeacherType.PERMANENT -> {
                when (LeaveType.PermanentType.valueOf(req.leaveType)) {
                    LeaveType.PermanentType.CASUAL_LEAVE -> {
                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.CASUAL_LEAVE.value
                        )

                        leaveType to applyCasualLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.EARNED_LEAVE -> {
                        // todo
                        return ApplyLeaveRes()
                    }

                    LeaveType.PermanentType.MEDICAL_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.MEDICAL_LEAVE.value
                        )

                        leaveType to applyMedicalLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.STUDY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.STUDY_LEAVE.value
                        )

                        leaveType to applyStudyLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.SPECIAL_STUDY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.SPECIAL_STUDY_LEAVE.value
                        )

                        leaveType to applySpecialStudyLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.MATERNITY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        if (teacherDetails.gender.uppercase() != "F") return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.MATERNITY_LEAVE.value
                        )

                        leaveType to applyMaternityLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.QUARANTINE_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.QUARANTINE_LEAVE.value
                        )

                        leaveType to applyQuarantineLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.COMMUTED_LEAVE -> {
                        TODO()
                    }

                    LeaveType.PermanentType.EXTRAORDINARY_LEAVE -> {
                        TODO()
                    }

                    LeaveType.PermanentType.COMPENSATORY_LEAVE -> {
                        TODO()
                    }

                    LeaveType.PermanentType.LEAVE_NOT_DUE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.LEAVE_NOT_DUE.value
                        )

                        leaveType to applyLeaveNotDueLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }

                    LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value
                        )

                        leaveType to applySpecialDisabilityLeave(
                            req = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = leaveType.id,
                                reqData = LocalDateTime.now(),
                                toDate = LocalDate.parse(req.toDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                fromDate = LocalDate.parse(req.fromDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                totalDays = req.totalDays.toDouble(),
                                reason = req.reason,
                                addressDuringLeave = req.addressDuringLeave,
                                pathId = path.id,
                                doc = doc
                            )
                        )
                    }
                }
            }
        }.let { (leaveType, response) ->
            when (response) {
                ApplyLeaveStatus.REJECTED.name -> ApplyLeaveRes()
                ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name -> ApplyLeaveRes(status = ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS)
                ApplyLeaveStatus.SOMETHING_WENT_WRONG.name -> ApplyLeaveRes(status = ApplyLeaveStatus.SOMETHING_WENT_WRONG)
                else -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        dbQuery {
                            LeaveBalanceTable.update(
                                where = {
                                    LeaveBalanceTable.teacherId eq teacher.id and (LeaveBalanceTable.leaveTypeId eq leaveType.id)
                                }
                            ) {
                                it[this.leaveBalance] = response.toDouble()
                            }
                        }
                    }

                    ApplyLeaveRes(
                        status = ApplyLeaveStatus.ACCEPTED,
                        newBalance = response
                    )
                }
            }
        }
    }


    private suspend fun applyCasualLeaveForSACTeacher(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 4.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.ScatType.CASUAL_LEAVE.value
            )
        }

        // checking if conflict with other leaves
        val isEmptyDef = async {
            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq req.teacherId and (LeaveReqTable.toDate greaterEq req.fromDate)
                }.empty()
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isEmpty = isEmptyDef.await()
        if (!isEmpty) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        addNewLeaveEntry(req)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMedicalLeaveForSACTeacher(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 20) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.ScatType.MEDICAL_LEAVE.value
            )
        }


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.MEDICAL_LEAVE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        // check if conflict with casual leave
        val isCasualEmpty = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.ScatType.CASUAL_LEAVE.value
        ).await()
        if (!isCasualEmpty) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        // check if conflict with other medial leaves
        recentEntryDef.await()?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        addNewLeaveEntry(req)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyStudyLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 360.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.ScatType.STUDY_LEAVE.value
            )
        }

        val serviceYearsDef = async {
            dbQuery {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.teacherId eq req.teacherId
                }.single().toTeacherDetails("")
                    .let {
                        val joiningDate = LocalDate.parse(it.joiningDate)
                        val reqDate = req.fromDate

                        ChronoUnit.YEARS.between(joiningDate, reqDate)
                    }
            }
        }

        val oldStudyLeaveEntryDef = async {
            dbQuery {
                val studyLeaveType = leaveUtils.getLeaveType(
                    type = LeaveType.ScatType.STUDY_LEAVE.value
                )

                LeaveReq.find {
                    LeaveReqTable.teacherId eq req.teacherId and
                            (LeaveReqTable.leaveTypeId eq studyLeaveType.id)
                }.toList()
            }
        }


        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val serviceYears = serviceYearsDef.await()
        if (serviceYears < 3L) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val oldStudyLeaveEntry = oldStudyLeaveEntryDef.await()


        if (oldStudyLeaveEntry.isEmpty()) addNewLeaveEntry(req)
        else {
            oldStudyLeaveEntry.forEach { // check for conflict
                if (req.fromDate <= it.toDate) return@coroutineScope ApplyLeaveStatus.REJECTED.name
            }

            // check does not exceed 24 months on total
            val totalDays = oldStudyLeaveEntry.sumOf {
                ChronoUnit.DAYS.between(it.fromDate, it.toDate)
            } + req.totalDays

            if (totalDays > 720) return@coroutineScope ApplyLeaveStatus.REJECTED.name

            // check there is 3 years diff in two req
            val mostRecentReq = oldStudyLeaveEntry.maxBy {
                it.toDate
            }.toDate

            if (ChronoUnit.YEARS.between(
                    req.reqData.toLocalDate(),
                    mostRecentReq
                ) < 3
            ) return@coroutineScope ApplyLeaveStatus.REJECTED.name

            addNewLeaveEntry(req)
        }

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyCasualLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 7.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.CASUAL_LEAVE.value
            )
        }

        // checking if conflict with other leaves
        val isEmptyDef = async {
            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq req.teacherId and
                            (LeaveReqTable.toDate greaterEq req.fromDate)
                }.empty()
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isEmpty = isEmptyDef.await()
        if (!isEmpty) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name



        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMedicalLeave(req: LeaveEntry) = coroutineScope {
        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.MEDICAL_LEAVE.value
            )
        }

        val isConflictWithQuarantineLeaveDef = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.PermanentType.QUARANTINE_LEAVE.value
        )

        val isConflictWithCasualLeaveDef = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.PermanentType.CASUAL_LEAVE.value
        )

        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.MEDICAL_LEAVE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithQuarantineLeave = !isConflictWithQuarantineLeaveDef.await()
        val isConflictWithCasualLeave = !isConflictWithCasualLeaveDef.await()
        val recentEntry = recentEntryDef.await()

        if (isConflictWithQuarantineLeave || isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        // check if conflict with other medial leaves
        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applySpecialStudyLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 360.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.SPECIAL_STUDY_LEAVE.value
            )
        }

        val serviceYearsDef = async {
            dbQuery {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.teacherId eq req.teacherId
                }.single().toTeacherDetails("")
                    .let {
                        val joiningDate = LocalDate.parse(it.joiningDate)
                        val reqDate = req.fromDate

                        ChronoUnit.YEARS.between(joiningDate, reqDate)
                    }
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val serviceYears = serviceYearsDef.await()
        if (serviceYears < 2L) return@coroutineScope ApplyLeaveStatus.REJECTED.name


        val monthDiffInApplyDateToFromDate = ChronoUnit.MONTHS.between(
            req.reqData.toLocalDate(),
            req.fromDate
        )

        if (monthDiffInApplyDateToFromDate < 3) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        addNewLeaveEntry(req)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMaternityLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 135.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.MATERNITY_LEAVE.value
            )
        }


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.MATERNITY_LEAVE.value,
            teacherId = req.teacherId.value
        )


        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        recentEntryDef.await()?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyQuarantineLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 21) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.QUARANTINE_LEAVE.value
            )
        }

        val conflictWithCasualLeaveDef = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.PermanentType.CASUAL_LEAVE.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val conflictWithCasualLeave = !conflictWithCasualLeaveDef.await()
        if (conflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyLeaveNotDueLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 90) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.LEAVE_NOT_DUE.value
            )
        }

        val isConflictWithCasualLeaveDef = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.PermanentType.LEAVE_NOT_DUE.value
        )


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.LEAVE_NOT_DUE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithCasualLeave = !isConflictWithCasualLeaveDef.await()
        val recentEntry = recentEntryDef.await()

        if (isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applySpecialDisabilityLeave(req: LeaveEntry) = coroutineScope {
        if (req.totalDays > 720) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value
            )
        }

        val isConflictWithCasualLeaveDef = checkIfConflictWithOtherLeave(
            teacherId = req.teacherId.value,
            toDate = req.toDate,
            leaveType = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value
        )


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithCasualLeave = !isConflictWithCasualLeaveDef.await()
        if (isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val recentEntry = recentEntryDef.await()
        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name
        }

        addNewLeaveEntry(req)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun addNewLeaveEntry(req: LeaveEntry) = dbQuery {
        LeaveReq.new {
            this.teacherId = req.teacherId
            this.leaveTypeId = req.leaveTypeId
            this.reqDate = req.reqData
            this.toDate = req.toDate
            this.fromDate = req.fromDate
            this.reason = req.reason
            this.addressDuringLeave = req.addressDuringLeave
            this.pathId = req.pathId
            this.doc = req.doc
        }
    }

    private suspend fun checkIfConflictWithOtherLeave(
        teacherId: Int,
        toDate: LocalDate,
        leaveType: String
    ) = coroutineScope {
        async {
            val leaveId = dbQuery {
                leaveUtils.getLeaveType(
                    type = leaveType
                ).id
            }

            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq teacherId and (LeaveReqTable.leaveTypeId eq leaveId.value) and
                            (LeaveReqTable.toDate greaterEq toDate) and
                            (LeaveReqTable.fromDate greaterEq toDate)
                }.empty()
            }
        }
    }

    private suspend fun getRecentEntry(
        type: String,
        teacherId: Int
    ) = coroutineScope {
        async {
            dbQuery {
                val leaveType = leaveUtils.getLeaveType(
                    type = type
                )

                LeaveReqTable.slice(LeaveReqTable.toDate.max())
                    .select {
                        LeaveReqTable.teacherId eq teacherId and
                                (LeaveReqTable.leaveTypeId eq leaveType.id)
                    }
                    .map { it[LeaveReqTable.toDate.max()] }
                    .singleOrNull()
            }
        }
    }
}