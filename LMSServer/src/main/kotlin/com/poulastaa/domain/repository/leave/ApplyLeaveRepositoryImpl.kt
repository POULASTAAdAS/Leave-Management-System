package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.data.model.leave.ApplyLeaveStatus
import com.poulastaa.data.model.leave.LeaveEntry
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.model.table.utils.PathTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.leave.ApplyLeaveRepository
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.utils.Path
import com.poulastaa.plugins.dbQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.sql.and
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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


        when (teacherType) {
            TeacherType.SACT -> {
                when (LeaveType.ScatType.valueOf(req.leaveType)) {
                    LeaveType.ScatType.CASUAL_LEAVE -> {
                        val id = dbQuery {
                            LeaveType.find {
                                LeaveTypeTable.type eq LeaveType.ScatType.CASUAL_LEAVE.name
                            }.single().id
                        }

                        val response = applyCasualLeaveForSACTeacher(
                            leave = LeaveEntry(
                                teacherId = teacher.id,
                                leaveTypeId = id,
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

                        return when {
                            response == ApplyLeaveStatus.REJECTED.name -> ApplyLeaveRes()
                            response == ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name -> ApplyLeaveRes(status = ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS)
                            response == ApplyLeaveStatus.SOMETHING_WENT_WRONG.name -> ApplyLeaveRes(status = ApplyLeaveStatus.SOMETHING_WENT_WRONG)
                            else -> ApplyLeaveRes(
                                status = ApplyLeaveStatus.ACCEPTED,
                                newBalance = response
                            )
                        }
                    }

                    LeaveType.ScatType.MEDICAL_LEAVE -> {

                    }

                    LeaveType.ScatType.STUDY_LEAVE -> {

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
                }
            }
        }

        return ApplyLeaveRes()
    }


    private suspend fun applyCasualLeaveForSACTeacher(
        leave: LeaveEntry
    ) = coroutineScope {
        val leaveBalanceDef = async {
            leaveUtils.getLeaveBalance(
                teacherId = leave.teacherId.value,
                type = LeaveType.ScatType.CASUAL_LEAVE.name
            )
        }

        val reasonDef = async {
            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.teacherId eq leave.teacherId and (LeaveReqTable.toDate greaterEq leave.fromDate)
                }.empty()
            }
        }

        val leaveBalance = leaveBalanceDef.await()?.toDouble() ?: return@coroutineScope ApplyLeaveStatus.REJECTED.name
        if (leaveBalance > leave.totalDays) return@coroutineScope ApplyLeaveStatus.REJECTED.name

        val response = reasonDef.await()
        if (response) return@coroutineScope ApplyLeaveStatus.A_REQ_HAS_ALREADY_EXISTS.name

        dbQuery {
            LeaveReq.new {
                this.teacherId = leave.teacherId
                this.leaveTypeId = leave.leaveTypeId
                this.reqDate = leave.reqData
                this.toDate = leave.toDate
                this.fromDate = leave.fromDate
                this.reason = leave.reason
                this.addressDuringLeave = leave.addressDuringLeave
                this.pathId = leave.pathId
                this.doc = leave.doc
            }
        }

        (leaveBalance - leave.totalDays).toString()
    }
}