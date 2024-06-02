package com.poulastaa.domain.repository.leave

import com.poulastaa.data.model.leave.LeaveHistoryRes
import com.poulastaa.data.model.table.leave.*
import com.poulastaa.data.model.table.utils.PendingEndTable
import com.poulastaa.data.model.table.utils.StatusTable
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.utils.PendingEnd
import com.poulastaa.domain.dao.utils.Status
import com.poulastaa.plugins.dbQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.upperCase
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

    override suspend fun getLeaves(
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
                            totalDays = ChronoUnit.DAYS.between(it.fromDate, it.toDate).toString()
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
}