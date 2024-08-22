package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.other.HeadType
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.leave.LeaveActionTable
import com.poulastaa.data.model.table.leave.LeaveBalanceTable
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.leave.LeaveStatusTable
import com.poulastaa.data.model.table.utils.PathTable
import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.leave.ApplyLeaveRepository
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.leave.LeaveAction
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.utils.Path
import com.poulastaa.domain.dao.utils.PendingEnd
import com.poulastaa.domain.dao.utils.Status
import com.poulastaa.plugins.query
import com.poulastaa.utils.toTeacherDetails
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ApplyLeaveRepositoryImpl(
    private val teacher: TeacherRepository,
    private val leaveUtils: LeaveUtilsRepository,
) : ApplyLeaveRepository {
    override suspend fun applyLeave(
        req: ApplyLeaveReq,
        doc: String?,
    ): ApplyLeaveRes {
        val teacher = teacher.getTeacher(req.email) ?: return ApplyLeaveRes()
        val teacherDetails = this.teacher.getTeacherDetails(req.email, teacher.id.value) ?: return ApplyLeaveRes()
        val teacherType = this.teacher.getTeacherTypeOnId(teacherDetails.teacherTypeId) ?: return ApplyLeaveRes()

        val path = query {
            Path.find {
                PathTable.type eq req.path
            }.singleOrNull()
        } ?: return ApplyLeaveRes()

        val pendingEndId = query {
            PendingEnd.find {
                PendingEndTable.type.upperCase() like "${path.type.split(' ')[0].trim().uppercase()}%"
            }.single().id.value
        }

        return when (teacherType) {
            TeacherType.SACT -> {
                val type = LeaveType.ScatType.valueOf(req.leaveType.trim().uppercase().replace(' ', '_'))

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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.ScatType.ON_DUTY_LEAVE -> {
                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.ON_DUTY_LEAVE.value
                        )

                        val leaveBalanceDef = leaveUtils.getLeaveBalance(
                            teacherId = teacher.id.value,
                            type = LeaveType.ScatType.ON_DUTY_LEAVE.value
                        )?.toDouble() ?: return ApplyLeaveRes()

                        handleNewLeaveEntry(
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )

                        leaveType to (leaveBalanceDef - req.totalDays.toDouble()).toString()
                    }

                    LeaveType.ScatType.MATERNITY_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.MATERNITY_LEAVE.value
                        )

                        val leaveBalanceDef = leaveUtils.getLeaveBalance(
                            teacherId = teacher.id.value,
                            type = LeaveType.ScatType.MATERNITY_LEAVE.value
                        )?.toDouble() ?: return ApplyLeaveRes()

                        handleNewLeaveEntry(
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )

                        leaveType to (leaveBalanceDef - req.totalDays.toDouble()).toString()
                    }

                    LeaveType.ScatType.QUARINTINE_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.ScatType.QUARINTINE_LEAVE.value
                        )

                        val leaveBalanceDef = leaveUtils.getLeaveBalance(
                            teacherId = teacher.id.value,
                            type = LeaveType.ScatType.QUARINTINE_LEAVE.value
                        )?.toDouble() ?: return ApplyLeaveRes()

                        handleNewLeaveEntry(
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )

                        leaveType to (leaveBalanceDef - req.totalDays.toDouble()).toString()
                    }
                }
            }

            TeacherType.PERMANENT -> {
                val type = LeaveType.PermanentType.valueOf(req.leaveType.uppercase().replace(' ', '_'))

                when (type) {
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.PermanentType.EARNED_LEAVE -> {
                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.EARNED_LEAVE.value
                        )

                        val leaveBalanceDef = leaveUtils.getLeaveBalance(
                            teacherId = teacher.id.value,
                            type = LeaveType.PermanentType.EARNED_LEAVE.value
                        )?.toDouble() ?: return ApplyLeaveRes()

                        handleNewLeaveEntry(
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )

                        leaveType to (leaveBalanceDef - req.totalDays.toDouble()).toString()
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.PermanentType.STUDY_LEAVE -> {
                        return ApplyLeaveRes()
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.PermanentType.QUARINTINE_LEAVE -> {
                        if (doc == null) return ApplyLeaveRes()

                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.QUARINTINE_LEAVE.value
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.PermanentType.COMMUTED_LEAVE -> {
                        return ApplyLeaveRes()
                    }

                    LeaveType.PermanentType.EXTRAORDINARY_LEAVE -> {
                        return ApplyLeaveRes()
                    }

                    LeaveType.PermanentType.COMPENSATORY_LEAVE -> {
                        return ApplyLeaveRes()
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )
                    }

                    LeaveType.PermanentType.ON_DUTY_LEAVE -> {
                        val leaveType = leaveUtils.getLeaveType(
                            type = LeaveType.PermanentType.ON_DUTY_LEAVE.value
                        )

                        val leaveBalanceDef = leaveUtils.getLeaveBalance(
                            teacherId = teacher.id.value,
                            type = LeaveType.ScatType.ON_DUTY_LEAVE.value
                        )?.toDouble() ?: return ApplyLeaveRes()

                        handleNewLeaveEntry(
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
                                doc = doc,
                                departmentId = teacherDetails.departmentId
                            ),
                            pendingEndId = pendingEndId
                        )

                        leaveType to (leaveBalanceDef - req.totalDays.toDouble()).toString()
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
                        query {
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


    // sac teacher
    private suspend fun applyCasualLeaveForSACTeacher(
        req: LeaveEntry,
        pendingEndId: Int,
    ) = coroutineScope {
        if (req.totalDays > 4.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name // greater than 4 days

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.ScatType.CASUAL_LEAVE.value
            )
        }

        // checking if conflict with other leaves
        val isEmptyDef = async {
            query {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq req.teacherId and
                            (LeaveReqTable.toDate greaterEq req.fromDate)
                }.empty()
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name // if balance less than total request days

        val isEmpty = isEmptyDef.await()
        if (!isEmpty) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        handleNewLeaveEntry(req, pendingEndId)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMedicalLeaveForSACTeacher(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
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
        val isEntry = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.CASUAL_LEAVE.value,
            fromDate = req.fromDate
        ).await()
        if (isEntry) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        // check if conflict with other medial leaves
        recentEntryDef.await()?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        handleNewLeaveEntry(req, pendingEndId)

        (leaveBalance - req.totalDays).toString()
    }

//    private suspend fun applyStudyLeave(
//        req: LeaveEntry,
//        pendingEndId: Int,
//    ) = coroutineScope {
//        if (req.totalDays > 360.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//
//        val leaveBalanceDef = async {
//            leaveUtils.getLeaveBalance(
//                teacherId = req.teacherId.value,
//                type = LeaveType.ScatType.STUDY_LEAVE.value
//            )
//        }
//
//        val serviceYearsDef = async {
//            query {
//                TeacherDetailsTable.select {
//                    TeacherDetailsTable.teacherId eq req.teacherId
//                }.single().toTeacherDetails("")
//                    .let {
//                        val joiningDate = LocalDate.parse(it.joiningDate)
//                        val reqDate = req.fromDate
//
//                        ChronoUnit.YEARS.between(joiningDate, reqDate)
//                    }
//            }
//        }
//
//        val oldStudyLeaveEntryDef = async {
//            query {
//                val studyLeaveType = leaveUtils.getLeaveType(
//                    type = LeaveType.ScatType.STUDY_LEAVE.value
//                )
//
//                LeaveReq.find {
//                    LeaveReqTable.teacherId eq req.teacherId and
//                            (LeaveReqTable.leaveTypeId eq studyLeaveType.id)
//                }.toList()
//            }
//        }
//
//
//        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
//        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//
//        val serviceYears = serviceYearsDef.await()
//        if (serviceYears < 3L) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//
//        val oldStudyLeaveEntry = oldStudyLeaveEntryDef.await()
//
//
//        if (oldStudyLeaveEntry.isEmpty()) handleNewLeaveEntry(req, pendingEndId)
//        else {
//            oldStudyLeaveEntry.forEach { // check for conflict
//                if (req.fromDate <= it.toDate) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//            }
//
//            // check does not exceed 24 months on total
//            val totalDays = oldStudyLeaveEntry.sumOf {
//                ChronoUnit.DAYS.between(it.fromDate, it.toDate)
//            } + req.totalDays
//
//            if (totalDays > 720) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//
//            // check there is 3 years diff in two req
//            val mostRecentReq = oldStudyLeaveEntry.maxBy {
//                it.toDate
//            }.toDate
//
//            if (ChronoUnit.YEARS.between(
//                    req.reqData.toLocalDate(),
//                    mostRecentReq
//                ) < 3
//            ) return@coroutineScope ApplyLeaveStatus.REJECTED.name
//
//            handleNewLeaveEntry(req, pendingEndId)
//        }
//
//        (leaveBalance - req.totalDays).toString()
//    }


    // permanent teacher
    private suspend fun applyCasualLeave(
        req: LeaveEntry,
        pendingEndId: Int,
    ) = coroutineScope {
        if (req.totalDays > 7.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.CASUAL_LEAVE.value
            )
        }

        // checking if conflict with other leaves
        val isEntryDef = async {
            query {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq req.teacherId and
                            (LeaveReqTable.toDate greaterEq req.fromDate)
                }.empty().not()
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isEntry = isEntryDef.await()
        if (isEntry) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name


        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMedicalLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.MEDICAL_LEAVE.value
            )
        }

        val isConflictWithQuarantineLeaveDef = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.QUARINTINE_LEAVE.value,
            fromDate = req.fromDate
        )

        val isConflictWithCasualLeaveDef = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.CASUAL_LEAVE.value,
            fromDate = req.fromDate
        )

        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.MEDICAL_LEAVE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithQuarantineLeave = isConflictWithQuarantineLeaveDef.await()
        val isConflictWithCasualLeave = isConflictWithCasualLeaveDef.await()
        val recentEntry = recentEntryDef.await()

        if (isConflictWithQuarantineLeave || isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        // check if conflict with other medial leaves
        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applySpecialStudyLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
        if (req.totalDays > 360.0) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.SPECIAL_STUDY_LEAVE.value
            )
        }

        val serviceYearsDef = async {
            query {
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

        handleNewLeaveEntry(req, pendingEndId)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyMaternityLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
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

        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyQuarantineLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
        if (req.totalDays > 21) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.QUARINTINE_LEAVE.value
            )
        }

        val conflictWithCasualLeaveDef = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.CASUAL_LEAVE.value,
            fromDate = req.fromDate
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val conflictWithCasualLeave = conflictWithCasualLeaveDef.await()
        if (conflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyLeaveNotDueLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
        if (req.totalDays > 90) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.LEAVE_NOT_DUE.value
            )
        }

        val isConflictWithCasualLeaveDef = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.LEAVE_NOT_DUE.value,
            fromDate = req.fromDate
        )


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.LEAVE_NOT_DUE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithCasualLeave = isConflictWithCasualLeaveDef.await()
        val recentEntry = recentEntryDef.await()

        if (isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applySpecialDisabilityLeave(req: LeaveEntry, pendingEndId: Int) = coroutineScope {
        if (req.totalDays > 720) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value
            )
        }

        val isConflictWithCasualLeaveDef = checkConflictCheckWithOtherLeave(
            teacherId = req.teacherId.value,
            leaveType = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value,
            fromDate = req.fromDate
        )


        val recentEntryDef = getRecentEntry(
            type = LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE.value,
            teacherId = req.teacherId.value
        )

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val isConflictWithCasualLeave = isConflictWithCasualLeaveDef.await()
        if (isConflictWithCasualLeave) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val recentEntry = recentEntryDef.await()
        recentEntry?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name
        }

        handleNewLeaveEntry(req, pendingEndId)
        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun checkConflictCheckWithOtherLeave(
        teacherId: Int,
        leaveType: String,
        fromDate: LocalDate,
    ) = coroutineScope {
        async {
            query {
                val leaveId = leaveUtils.getLeaveType(
                    type = leaveType
                ).id

                LeaveReq.find {
                    LeaveReqTable.teacherId eq teacherId and
                            (LeaveReqTable.leaveTypeId eq leaveId) and
                            (LeaveReqTable.toDate greaterEq fromDate)
                }.empty().not()
            }
        }
    }

    private suspend fun handleNewLeaveEntry(
        req: LeaveEntry,
        pendingEndId: Int,
    ) = coroutineScope {
        val newEntry = query {
            LeaveReq.new { // create leave entry
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

        val statusIdDef = async { leaveUtils.getPendingStatusId() }

        val statusId = statusIdDef.await()
        query {
            LeaveStatusTable.insert {
                it[this.leaveId] = newEntry.id.value
                it[this.statusId] = statusId
                it[this.pendingEndId] = pendingEndId
                it[this.departmentId] = req.departmentId
                it[this.cause] = ""
                it[this.actionId] = null
            }
        }
    }

    private suspend fun getRecentEntry(
        type: String,
        teacherId: Int,
    ) = coroutineScope {
        async {
            query {
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

    override suspend fun handleLeave(
        req: HandleLeaveReq,
        headType: HeadType,
    ): Pair<LeaveAction.TYPE, Int> = coroutineScope {
        val actionType = when {
            req.action.startsWith("Accept") -> LeaveAction.TYPE.FORWARD
            req.action.startsWith("Approve") -> LeaveAction.TYPE.APPROVED
            else -> LeaveAction.TYPE.REJECT
        }

        val actionDef = async {
            query {
                LeaveAction.find {
                    LeaveActionTable.type.upperCase() eq actionType.value.uppercase()
                }.single()
            }
        }

        val statusType = when (actionType) {
            LeaveAction.TYPE.APPROVED -> Status.TYPE.APPROVED
            LeaveAction.TYPE.FORWARD -> Status.TYPE.PENDING
            LeaveAction.TYPE.REJECT -> Status.TYPE.REJECTED
        }

        val statusDef = async {
            query {
                Status.find {
                    StatusTable.type.upperCase() eq statusType.value.uppercase()
                }.single()
            }
        }

        val pendingType = when (actionType) {
            LeaveAction.TYPE.APPROVED -> if (headType == HeadType.PRINCIPAL) PendingEnd.TYPE.NOT_PENDING else PendingEnd.TYPE.PRINCIPLE_LEVEL
            LeaveAction.TYPE.FORWARD -> PendingEnd.TYPE.PRINCIPLE_LEVEL
            LeaveAction.TYPE.REJECT -> PendingEnd.TYPE.NOT_PENDING
        }

        val pendingEndDef = async {
            query {
                PendingEnd.find {
                    PendingEndTable.type.upperCase() eq pendingType.value.uppercase()
                }.single()
            }
        }

        val teacherIdDef = async {
            query {
                LeaveReq.find {
                    LeaveReqTable.id eq req.leaveId
                }.single().teacherId.value
            }
        }

        val action = actionDef.await()
        val status = statusDef.await()
        val pendingEnd = pendingEndDef.await()
        val teacherId = teacherIdDef.await()

        query {
            LeaveStatusTable.update(
                where = {
                    LeaveStatusTable.leaveId eq req.leaveId
                }
            ) {
                it[this.statusId] = status.id
                if (headType == HeadType.PRINCIPAL) it[this.approveDate] = LocalDate.now()
                it[this.pendingEndId] = pendingEnd.id
                it[this.cause] = req.cause
                it[this.actionId] = action.id
            }
        }

        if (actionType == LeaveAction.TYPE.REJECT) {
            CoroutineScope(Dispatchers.IO).launch {
                val leaveReq = query {
                    LeaveReq.find {
                        LeaveReqTable.id eq req.leaveId
                    }.single()
                }

                val totalDays = (ChronoUnit.DAYS.between(
                    leaveReq.fromDate,
                    leaveReq.toDate
                ) + 1).toInt()

                val currentBalance = query {
                    LeaveBalanceTable.select {
                        LeaveBalanceTable.teacherId eq teacherId and (LeaveBalanceTable.leaveTypeId eq leaveReq.leaveTypeId)
                    }.first().let {
                        it[LeaveBalanceTable.leaveBalance]
                    }
                }

                query {
                    LeaveBalanceTable.update(
                        where = {
                            LeaveBalanceTable.teacherId eq teacherId and (LeaveBalanceTable.leaveTypeId eq leaveReq.leaveTypeId)
                        }
                    ) {
                        it[this.leaveBalance] = currentBalance + totalDays
                    }
                }
            }
        }

        actionType to teacherId
    }
}