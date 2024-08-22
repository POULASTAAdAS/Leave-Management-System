package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.other.HeadType
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.leave.LeaveBalanceTable
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.leave.LeaveStatusTable
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.domain.dao.utils.PendingEnd
import com.poulastaa.domain.dao.utils.Status
import com.poulastaa.plugins.query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class LeaveUtilsRepositoryImpl : LeaveUtilsRepository {
    override suspend fun getLeaveType(type: String) = query {
        LeaveType.find {
            LeaveTypeTable.type.upperCase() eq type.uppercase().replace(Regex("\\(.*\\)"), "").trim()
        }.single()
    }

    override suspend fun getLeaveBalance(
        teacherId: Int,
        type: String,
    ): String? = query {
        val entry = getLeaveType(type)

        LeaveBalanceTable.select {
            LeaveBalanceTable.teacherId eq teacherId and (LeaveBalanceTable.leaveTypeId eq entry.id)
        }.singleOrNull()?.let {
            it[LeaveBalanceTable.leaveBalance].toString()
        }
    }

    override suspend fun getHistoryLeaves(
        teacherId: Int,
        page: Int,
        pageSize: Int,
    ): List<LeaveHistoryRes> = coroutineScope {
        data class StatusPayload(
            val leaveStatusId: Int,
            val pendingEndId: Int,
            val approveDate: String,
        )

        query {
            LeaveReq.find {
                LeaveReqTable.teacherId eq teacherId
            }.orderBy(LeaveReqTable.reqDate to SortOrder.DESC)
                .drop(if (page == 1) 0 else page * pageSize)
                .take(pageSize)
                .map { req ->
                    async {
                        val leaveType = async {
                            query {
                                LeaveType.find {
                                    LeaveTypeTable.id eq req.leaveTypeId
                                }.single().type
                            }
                        }

                        val payload = query {
                            LeaveStatusTable.slice(
                                LeaveStatusTable.statusId,
                                LeaveStatusTable.approveDate,
                                LeaveStatusTable.pendingEndId
                            ).select {
                                LeaveStatusTable.leaveId eq req.id
                            }.single().let { leaStatusRes ->
                                StatusPayload(
                                    leaveStatusId = leaStatusRes[LeaveStatusTable.statusId].value,
                                    pendingEndId = leaStatusRes[LeaveStatusTable.pendingEndId].value,
                                    approveDate = leaStatusRes[LeaveStatusTable.approveDate]?.format(
                                        DateTimeFormatter.ofPattern(
                                            "dd-MM-yyyy"
                                        )
                                    ) ?: ""
                                )
                            }
                        }

                        val status = async {
                            query {
                                Status.find {
                                    StatusTable.id eq payload.leaveStatusId
                                }.single().type
                            }
                        }

                        val pendingEnd = async {
                            query {
                                PendingEnd.find {
                                    PendingEndTable.id eq payload.pendingEndId
                                }.single().type
                            }
                        }

                        LeaveHistoryRes(
                            reqDate = req.reqDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            approveDate = payload.approveDate,
                            leaveType = leaveType.await(),
                            status = status.await(),
                            fromDate = req.fromDate.toString(),
                            toDate = req.toDate.toString(),
                            pendingEnd = pendingEnd.await(),
                            totalDays = (ChronoUnit.DAYS.between(req.fromDate, req.toDate) + 1L).toString()
                        )
                    }
                }.awaitAll()
        }
    }

    override suspend fun getPendingEndId(
        isPermanent: Boolean,
    ): EntityID<Int> = query {
        PendingEnd.find {
            PendingEndTable.type eq if (isPermanent) PendingEnd.TYPE.PRINCIPLE_LEVEL.value else PendingEnd.TYPE.DEPARTMENT_LEVEL.value
        }.single().id
    }

    override suspend fun getPendingStatusId(): EntityID<Int> = query {
        Status.find {
            StatusTable.type eq Status.TYPE.PENDING.value
        }.single().id
    }

    override suspend fun getApproveLeaveAsDepartmentHead(
        departmentId: Int,
        teacherHeadId: Int,
        page: Int,
        pageSize: Int,
    ): List<LeaveApproveRes> =
        coroutineScope {
            val departmentPendingEnd = query {
                PendingEnd.find {
                    PendingEndTable.type eq "Department Level"
                }.first().id.value
            }

            val leaveId = query {
                LeaveStatusTable
                    .slice(LeaveStatusTable.leaveId)
                    .select {
                        LeaveStatusTable.departmentId eq departmentId and (LeaveStatusTable.pendingEndId eq departmentPendingEnd)
                    }.map {
                        it[LeaveStatusTable.leaveId].value
                    }
            }

            if (leaveId.isEmpty()) return@coroutineScope emptyList()

            query {
                LeaveReq.find {
                    LeaveReqTable.id inList leaveId and (LeaveReqTable.teacherId notInList listOf(teacherHeadId))
                }.orderBy(LeaveReqTable.reqDate to SortOrder.ASC)
                    .drop(if (page == 1) 0 else page * pageSize)
                    .take(pageSize)
                    .toApproveLeaveRes()
                    .awaitAll()
            }
        }

    override suspend fun getApproveLeaveAsHead(
        page: Int,
        pageSize: Int,
        isPrincipal: Boolean,
    ): List<LeaveApproveRes> = coroutineScope {
        val pendingId = query {
            PendingEnd.find {
                PendingEndTable.type eq if (isPrincipal) "Principal Level" else "Head Clark Level"
            }.single().id.value
        }

        val leaveId = query {
            LeaveStatusTable
                .slice(LeaveStatusTable.leaveId)
                .select {
                    LeaveStatusTable.pendingEndId eq pendingId
                }.map {
                    it[LeaveStatusTable.leaveId].value
                }
        }

        if (leaveId.isEmpty()) return@coroutineScope emptyList()

        query {
            LeaveReq.find {
                LeaveReqTable.id inList leaveId
            }.orderBy(LeaveReqTable.reqDate to SortOrder.ASC)
                .drop(if (page == 1) 0 else page * pageSize)
                .take(pageSize)
                .toApproveLeaveRes()
                .awaitAll()
        }
    }

    override suspend fun getLeaveOnId(leaveId: Long): LeaveReq = query {
        LeaveReq.find {
            LeaveReqTable.id eq leaveId
        }.single()
    }

    private suspend fun List<LeaveReq>.toApproveLeaveRes() = coroutineScope {
        this@toApproveLeaveRes.map {
            async {
                val name = async {
                    query {
                        TeacherDetailsTable
                            .slice(TeacherDetailsTable.name)
                            .select {
                                TeacherDetailsTable.teacherId eq it.teacherId
                            }.single().let { res ->
                                res[TeacherDetailsTable.name]
                            }
                    }
                }
                val leaveType = async {
                    query {
                        LeaveType.find {
                            LeaveTypeTable.id eq it.leaveTypeId
                        }.single().type
                    }
                }

                LeaveApproveRes(
                    leaveId = it.id.value,
                    reqData = it.reqDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    name = name.await(),
                    leaveType = leaveType.await(),
                    fromDate = it.fromDate.toString(),
                    toDate = it.toDate.toString(),
                    totalDays = (ChronoUnit.DAYS.between(it.fromDate, it.toDate) + 1L).toString(),
                    docUrl = it.doc?.let { "${System.getenv("BASE_URL")}/${EndPoints.GetDoc.route}?image=$it" }
                )
            }
        }
    }

    override suspend fun viewLeave(
        dpId: Int,
        teacherId: Int,
        email: String,
        page: Int,
        pageSize: Int,
        headType: HeadType,
    ): List<ViewLeaveSingleRes> = coroutineScope {
        val join = LeaveReqTable
            .join(
                otherTable = LeaveStatusTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    LeaveReqTable.id eq LeaveStatusTable.leaveId
                }
            ).join(
                otherTable = TeacherDetailsTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    TeacherDetailsTable.teacherId eq LeaveReqTable.teacherId
                }
            ).join(
                otherTable = LeaveTypeTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    LeaveTypeTable.id eq LeaveReqTable.leaveTypeId
                }
            ).join(
                otherTable = StatusTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    StatusTable.id eq LeaveStatusTable.statusId
                }
            ).join(
                otherTable = DepartmentTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    DepartmentTable.id eq LeaveStatusTable.departmentId
                }
            )
            .slice(
                TeacherDetailsTable.teacherId,
                TeacherDetailsTable.name,
                LeaveTypeTable.type,
                LeaveReqTable.reqDate,
                LeaveReqTable.fromDate,
                LeaveReqTable.toDate,
                StatusTable.type,
                LeaveStatusTable.cause,
                DepartmentTable.name
            )

        when (headType) {
            HeadType.HOD -> {
                val teacher = query {
                    Teacher.find {
                        TeacherTable.email eq email
                    }.singleOrNull()
                } ?: return@coroutineScope emptyList()

                val departmentId = query {
                    DepartmentHead.find {
                        DepartmentHeadTable.teacherId eq teacher.id
                    }.singleOrNull()?.departmentId
                } ?: return@coroutineScope emptyList()

                query {
                    val pendingEndId = PendingEnd.find {
                        PendingEndTable.type eq PendingEnd.TYPE.NOT_PENDING.value
                    }.single().id


                    if (teacherId != -1) join.select {
                        LeaveStatusTable.pendingEndId eq pendingEndId and (DepartmentTable.id eq departmentId) and (TeacherDetailsTable.teacherId eq teacherId)
                    }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    else join.select {
                        LeaveStatusTable.pendingEndId eq pendingEndId and (DepartmentTable.id eq departmentId)
                    }.orderBy(DepartmentTable.id, SortOrder.ASC)
                }
            }

            HeadType.PRINCIPAL,
            HeadType.HEAD_CLARK,
                -> {
                val pendingEndId = query {
                    PendingEnd.find {
                        PendingEndTable.type eq PendingEnd.TYPE.NOT_PENDING.value
                    }.single().id
                }


                if (dpId != -1) {
                    if (teacherId != -1) query {
                        join.select {
                            LeaveStatusTable.pendingEndId eq pendingEndId and (DepartmentTable.id eq dpId) and (TeacherDetailsTable.teacherId eq teacherId)
                        }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    }
                    else query {
                        join.select {
                            LeaveStatusTable.pendingEndId eq pendingEndId and (DepartmentTable.id eq dpId)
                        }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    }
                } else {
                    if (teacherId != -1) query {
                        join.select {
                            LeaveStatusTable.pendingEndId eq pendingEndId and (TeacherDetailsTable.teacherId eq teacherId)
                        }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    }
                    else query {
                        join.select {
                            LeaveStatusTable.pendingEndId eq pendingEndId
                        }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    }
                }
            }
        }.let {
            query {
                it.asSequence()
                    .drop(if (page == 1) 0 else page * pageSize)
                    .take(pageSize)
                    .map { row ->
                        ViewLeaveEntry(
                            name = row[TeacherDetailsTable.name],
                            leaveType = row[LeaveTypeTable.type],
                            reqData = row[LeaveReqTable.reqDate].format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            fromDate = row[LeaveReqTable.fromDate].toString(),
                            toDate = row[LeaveReqTable.fromDate].toString(),
                            totalDays = (ChronoUnit.DAYS.between(
                                row[LeaveReqTable.fromDate],
                                row[LeaveReqTable.toDate]
                            ) + 1).toString(),
                            status = row[StatusTable.type],
                            cause = row[LeaveStatusTable.cause],
                            department = row[DepartmentTable.name].trim()
                        )
                    }.groupBy { grp ->
                        grp.department
                    }.map { entry ->
                        ViewLeaveSingleRes(
                            department = entry.key,
                            listOfLeave = entry.value.map { info ->
                                ViewLeaveInfoRes(
                                    name = info.name,
                                    leaveType = info.leaveType,
                                    reqData = info.reqData,
                                    fromDate = info.fromDate,
                                    toDate = info.toDate,
                                    totalDays = info.totalDays,
                                    status = info.status,
                                    cause = info.cause
                                )
                            }
                        )
                    }.toList()
            }
        }
    }
}
