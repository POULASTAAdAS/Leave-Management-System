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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
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

                        leaveType to applyStudyLeaveForSACTeacher(
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

            TeacherType.PERMANENT -> {
                when (LeaveType.PermanentType.valueOf(req.leaveType)) {
                    LeaveType.PermanentType.CASUAL_LEAVE -> {

                    }

                    LeaveType.PermanentType.MEDICAL_LEAVE -> {

                    }

                    LeaveType.PermanentType.STUDY_LEAVE -> {

                    }

                    LeaveType.PermanentType.EARNED_LEAVE -> {

                    }

                    LeaveType.PermanentType.SPECIAL_STUDY_LEAVE -> {

                    }

                    LeaveType.PermanentType.MATERNITY_LEAVE -> {

                    }

                    LeaveType.PermanentType.QUARANTINE_LEAVE -> {

                    }

                    LeaveType.PermanentType.COMMUTED_LEAVE -> {

                    }

                    LeaveType.PermanentType.EXTRAORDINARY_LEAVE -> {

                    }

                    LeaveType.PermanentType.COMPENSATORY_LEAVE -> {

                    }

                    LeaveType.PermanentType.LEAVE_NOT_DUE -> {

                    }

                    LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE -> {

                    }
                }.let {
                    ApplyLeaveRes()
                }
            }
        }
    }


    private suspend fun applyCasualLeaveForSACTeacher(
        req: LeaveEntry
    ) = coroutineScope {
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

    private suspend fun applyMedicalLeaveForSACTeacher(
        req: LeaveEntry
    ) = coroutineScope {
        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = req.teacherId.value,
                type = LeaveType.ScatType.MEDICAL_LEAVE.value
            )
        }


        val recentEntryDef = async {
            dbQuery {
                val medicalLeaveType = leaveUtils.getLeaveType(
                    type = LeaveType.ScatType.STUDY_LEAVE.value
                )

                LeaveReqTable.slice(LeaveReqTable.toDate.max())
                    .select {
                        LeaveReqTable.teacherId eq req.teacherId and (LeaveReqTable.leaveTypeId eq medicalLeaveType.id)
                    }
                    .map { it[LeaveReqTable.toDate.max()] }
                    .singleOrNull()
            }
        }


        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance < req.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        // check if conflict with casual leave
        val isCasualEmpty = checkIfConflictWithCasualLeave(req.teacherId.value, req.toDate).await()
        if (!isCasualEmpty) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        // check if conflict with other medial leaves
        recentEntryDef.await()?.let {
            if (req.fromDate <= it) return@coroutineScope ApplyLeaveStatus.REJECTED.name
        }

        addNewLeaveEntry(req)

        (leaveBalance - req.totalDays).toString()
    }

    private suspend fun applyStudyLeaveForSACTeacher(
        req: LeaveEntry
    ) = coroutineScope {
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
                        val reqDate = req.toDate

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
                    LeaveReqTable.teacherId eq req.teacherId and (LeaveReqTable.leaveTypeId eq studyLeaveType.id)
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

    private suspend fun checkIfConflictWithCasualLeave(
        teacherId: Int,
        toDate: LocalDate
    ) = coroutineScope {
        async {
            val casualLeaveId = dbQuery {
                leaveUtils.getLeaveType(
                    type = LeaveType.ScatType.CASUAL_LEAVE.value
                ).id
            }

            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq teacherId and (LeaveReqTable.leaveTypeId eq casualLeaveId.value) and
                            (LeaveReqTable.toDate greaterEq toDate) and
                            (LeaveReqTable.fromDate greaterEq toDate)
                }.empty()
            }
        }
    }
}