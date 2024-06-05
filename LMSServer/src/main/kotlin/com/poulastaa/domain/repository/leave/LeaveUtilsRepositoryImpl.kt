package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.leave.*
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.leave.LeaveAction
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.domain.dao.utils.PendingEnd
import com.poulastaa.domain.dao.utils.Status
import com.poulastaa.plugins.dbQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class LeaveUtilsRepositoryImpl : LeaveUtilsRepository {
    override suspend fun getLeaveType(type: String) = dbQuery {
        LeaveType.find {
            LeaveTypeTable.type.upperCase() eq type.uppercase()
        }.single()
    }

    override suspend fun getLeaveBalance(
        teacherId: Int,
        type: String
    ): String? = dbQuery {
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
        pageSize: Int
    ): List<LeaveHistoryRes> = coroutineScope {
        dbQuery {
            LeaveReq.find {
                LeaveReqTable.teacherId eq teacherId
            }.orderBy(LeaveReqTable.reqDate to SortOrder.DESC)
                .drop(if (page == 1) 0 else page * pageSize)
                .take(pageSize)
                .map {
                    async {
                        val leaveType = async {
                            dbQuery {
                                LeaveType.find {
                                    LeaveTypeTable.id eq it.leaveTypeId
                                }.single().type
                            }
                        }

                        val (leaveStatusId, pendingEndId) = dbQuery {
                            LeaveStatusTable.slice(
                                LeaveStatusTable.statusId,
                                LeaveStatusTable.pendingEndId
                            ).select {
                                LeaveStatusTable.leaveId eq it.id
                            }.single().let { leaStatusRes ->
                                leaStatusRes[LeaveStatusTable.statusId].value to
                                        leaStatusRes[LeaveStatusTable.pendingEndId].value
                            }
                        }

                        val status = async {
                            dbQuery {
                                Status.find {
                                    StatusTable.id eq leaveStatusId
                                }.single().type
                            }
                        }

                        val pendingEnd = async {
                            dbQuery {
                                PendingEnd.find {
                                    PendingEndTable.id eq pendingEndId
                                }.single().type
                            }
                        }

                        LeaveHistoryRes(
                            reqDate = it.reqDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            leaveType = leaveType.await(),
                            status = status.await(),
                            fromDate = it.fromDate.toString(),
                            toDate = it.toDate.toString(),
                            pendingEnd = pendingEnd.await(),
                            totalDays = (ChronoUnit.DAYS.between(it.fromDate, it.toDate) + 1L).toString()
                        )
                    }
                }.awaitAll()
        }
    }

    override suspend fun getPendingEndId(
        isPermanent: Boolean
    ): EntityID<Int> = dbQuery {
        PendingEnd.find {
            PendingEndTable.type eq if (isPermanent) PendingEnd.TYPE.PRINCIPLE_LEVEL.value else PendingEnd.TYPE.DEPARTMENT_LEVEL.value
        }.single().id
    }

    override suspend fun getPendingStatusId(): EntityID<Int> = dbQuery {
        Status.find {
            StatusTable.type eq Status.TYPE.PENDING.value
        }.single().id
    }

    override suspend fun getApproveLeaveAsDepartmentHead(
        departmentId: Int,
        teacherHeadId: Int,
        page: Int,
        pageSize: Int
    ): List<LeaveApproveRes> =
        coroutineScope {
            val leaveId = dbQuery {
                LeaveStatusTable
                    .slice(LeaveStatusTable.leaveId)
                    .select {
                        LeaveStatusTable.departmentId eq departmentId and (LeaveStatusTable.actionId eq null)
                    }.map {
                        it[LeaveStatusTable.leaveId].value
                    }
            }

            if (leaveId.isEmpty()) return@coroutineScope emptyList()

            dbQuery {
                LeaveReq.find {
                    LeaveReqTable.id inList leaveId and (LeaveReqTable.teacherId notInList listOf(teacherHeadId))
                }.orderBy(LeaveReqTable.reqDate to SortOrder.ASC)
                    .drop(if (page == 1) 0 else page * pageSize)
                    .take(pageSize)
                    .toApproveLeaveRes()
                    .awaitAll()
            }
        }

    override suspend fun getApproveLeaveAsHead(page: Int, pageSize: Int): List<LeaveApproveRes> = coroutineScope {
        val forwardId = dbQuery {
            LeaveAction.find {
                LeaveActionTable.type eq LeaveAction.TYPE.FORWARD.value
            }.single().id
        }

        val leaveId = dbQuery {
            LeaveStatusTable
                .slice(LeaveStatusTable.leaveId)
                .select {
                    LeaveStatusTable.actionId eq forwardId or (LeaveStatusTable.actionId eq null)
                }.map {
                    it[LeaveStatusTable.leaveId].value
                }
        }

        if (leaveId.isEmpty()) return@coroutineScope emptyList()

        dbQuery {
            LeaveReq.find {
                LeaveReqTable.id inList leaveId
            }.orderBy(LeaveReqTable.reqDate to SortOrder.ASC)
                .drop(if (page == 1) 0 else page * pageSize)
                .take(pageSize)
                .toApproveLeaveRes()
                .awaitAll()
        }
    }

    override suspend fun getLeaveOnId(leaveId: Long): LeaveReq = dbQuery {
        LeaveReq.find {
            LeaveReqTable.id eq leaveId
        }.single()
    }

    private suspend fun List<LeaveReq>.toApproveLeaveRes() = coroutineScope {
        this@toApproveLeaveRes.map {
            async {
                val name = async {
                    dbQuery {
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
                    dbQuery {
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
                    totalDays = (ChronoUnit.DAYS.between(it.fromDate, it.toDate) + 1L).toString()
                )
            }
        }
    }

    override suspend fun viewLeave(
        email: String,
        page: Int,
        pageSize: Int,
        isPrinciple: Boolean,
    ): List<ViewLeaveSingleRes> = coroutineScope {
        if (isPrinciple) {
            dbQuery {
                val pendingEndId = PendingEnd.find {
                    PendingEndTable.type eq PendingEnd.TYPE.NOT_PENDING.value
                }.single().id

                LeaveReqTable
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
                        TeacherDetailsTable.name,
                        LeaveTypeTable.type,
                        LeaveReqTable.reqDate,
                        LeaveReqTable.fromDate,
                        LeaveReqTable.toDate,
                        StatusTable.type,
                        LeaveStatusTable.cause,
                        DepartmentTable.name
                    ).select {
                        LeaveStatusTable.pendingEndId eq pendingEndId
                    }.orderBy(DepartmentTable.id, SortOrder.ASC)
                    .asSequence()
                    .drop(if (page == 1) 0 else page * pageSize)
                    .take(pageSize)
                    .map {
                        ViewLeaveEntry(
                            name = it[TeacherDetailsTable.name],
                            leaveType = it[LeaveTypeTable.type],
                            reqData = it[LeaveReqTable.reqDate].format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            fromDate = it[LeaveReqTable.fromDate].toString(),
                            toDate = it[LeaveReqTable.fromDate].toString(),
                            totalDays = (ChronoUnit.DAYS.between(
                                it[LeaveReqTable.fromDate],
                                it[LeaveReqTable.toDate]
                            ) + 1).toString(),
                            status = it[StatusTable.type],
                            cause = it[LeaveStatusTable.cause],
                            department = it[DepartmentTable.name].trim()
                        )
                    }.groupBy {
                        it.department
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
        } else {
            val teacher = dbQuery {
                Teacher.find {
                    TeacherTable.email eq email
                }.singleOrNull()
            } ?: return@coroutineScope emptyList()

            val departmentId = dbQuery {
                DepartmentHead.find {
                    DepartmentHeadTable.teacherId eq teacher.id
                }.singleOrNull()?.departmentId
            } ?: return@coroutineScope emptyList()


            dbQuery {

            }

            emptyList()
        }
    }
}

