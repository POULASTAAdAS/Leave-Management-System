package com.poulastaa.domain.repository

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.req.AddTeacherReq
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.department.DepartmentTeacher
import com.poulastaa.data.model.department.GetDepartmentInChargeRes
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.details.UpdateHeadDetailsReq
import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.other.*
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.designation.DesignationTable
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.leave.LeaveStatusTable
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.model.table.session.SessionStorageTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import com.poulastaa.data.model.table.utils.PathTable
import com.poulastaa.data.report.LeaveData
import com.poulastaa.data.report.ReportResponse
import com.poulastaa.data.report.ReportResult
import com.poulastaa.data.repository.JWTRepository
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.leave.LeaveWrapper
import com.poulastaa.domain.dao.department.Department
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.leave.LeaveAction
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.session.SessionStorageDB
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.domain.dao.teacher.TeacherType
import com.poulastaa.domain.dao.utils.Designation
import com.poulastaa.domain.dao.utils.HeadClark
import com.poulastaa.domain.dao.utils.Path
import com.poulastaa.domain.dao.utils.Principal
import com.poulastaa.invalidTokenList
import com.poulastaa.plugins.query
import com.poulastaa.utils.Constants
import com.poulastaa.utils.Constants.LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.Constants.PROFILE_FOLDER_PATH
import com.poulastaa.utils.Constants.SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.sendEmail
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import java.io.File
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val PENDING_STATUS_CODE = 2

class ServiceRepositoryImpl(
    private val jwtRepo: JWTRepository,
    private val teacher: TeacherRepository,
    private val leave: LeaveWrapper,
) : ServiceRepository {
    override suspend fun auth(email: String): AuthRes {
        if (!isValidEmail(email)) return AuthRes()

        val response = teacher.getTeacherDetailsStatus(email)

        return when (response.first) {
            AuthStatus.PRINCIPLE_FOUND -> {
                val token = jwtRepo.generateLogInVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifyLogInEmail.route)

                val principle = response.second as Principal

                AuthRes(
                    authStatus = response.first,
                    user = User(
                        name = principle.name,
                        email = principle.email,
                        profilePicUrl = Constants.BASE_URL + EndPoints.GetProfilePic.route
                    )
                )
            }

            AuthStatus.SIGNUP -> {
                val token = jwtRepo.generateSignUpVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifySignUpEmail.route)

                AuthRes(
                    authStatus = response.first
                )
            }

            AuthStatus.LOGIN -> {
                val token = jwtRepo.generateLogInVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifyLogInEmail.route)

                val user = teacher.getTeacherWithDetails(email)

                AuthRes(
                    authStatus = response.first,
                    user = user
                )
            }

            AuthStatus.EMAIL_NOT_REGISTERED -> {
                AuthRes()
            }

            AuthStatus.HEAD_CLARK_FOUND -> {
                val token = jwtRepo.generateLogInVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifyLogInEmail.route)

                val headClark = response.second as HeadClark

                AuthRes(
                    authStatus = response.first,
                    user = User(
                        name = headClark.name,
                        email = headClark.email,
                        profilePicUrl = Constants.BASE_URL + EndPoints.GetProfilePic.route
                    )
                )
            }
        }
    }

    override suspend fun updateSignUpVerificationStatus(token: String): VerifiedMailStatus {
        val result = jwtRepo.verifyJWTToken(token, SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY)
            ?: return VerifiedMailStatus.TOKEN_NOT_VALID

        if (result == VerifiedMailStatus.TOKEN_USED.name) return VerifiedMailStatus.TOKEN_USED

        invalidTokenList.add(token)

        val response = teacher.updateSignUpVerificationStatus(result)

        return response
    }

    override suspend fun updateLogInVerificationStatus(token: String): Pair<VerifiedMailStatus, Pair<String, String>> {
        val result = jwtRepo.verifyJWTToken(token, LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY)
            ?: return VerifiedMailStatus.TOKEN_NOT_VALID to Pair("", "")

        if (result == VerifiedMailStatus.TOKEN_USED.name) return VerifiedMailStatus.TOKEN_USED to Pair("", "")

        invalidTokenList.add(token)

        val response = teacher.updateLogInVerificationStatus(result)

        return response
    }

    override suspend fun signUpEmailVerificationCheck(email: String): EmailVerificationRes {
        val status = teacher.signupEmailVerificationCheck(email)

        return if (status) EmailVerificationRes(status = true)
        else EmailVerificationRes()
    }

    override suspend fun loginEmailVerificationCheck(email: String): Pair<EmailVerificationRes, String?> {
        val status = teacher.loginEmailVerificationCheck(email)

        return if (status) {
            val teacherName = teacher.getTeacherWithDetails(email).name
            val name = teacherName.ifEmpty { getPrinciple().name }

            EmailVerificationRes(status = true) to name
        } else EmailVerificationRes() to null
    }

    override suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes {
        if (!req.validateDetails()) return SetDetailsRes()

        return teacher.saveTeacherDetails(req)
    }

    override suspend fun getTeacherDetails(email: String): GetTeacherRes? = teacher.getTeacherDetailsRes(email)

    override suspend fun updateDetails(
        email: String,
        req: UpdateDetailsReq,
    ): Boolean {
        if (req.email.isNotEmpty() && !isValidEmail(req.email)) return false

        return teacher.updateDetails(email, req)
    }

    override suspend fun updateHeadDetails(email: String, req: UpdateHeadDetailsReq): Boolean {
        if (req.email.isNotEmpty() && !isValidEmail(req.email)) return false

        return teacher.updateHeadDetails(email, req)
    }

    override suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean =
        teacher.updateAddress(email, req)

    override suspend fun storeProfilePic(
        email: String,
        name: String,
    ): Boolean = teacher.storeProfilePic(
        email = email,
        fileNameWithPath = name,
    )


    override suspend fun getProfilePic(email: String): File? = try {
        teacher.getProfilePic(email)?.let { File("${PROFILE_FOLDER_PATH}$it") }
    } catch (_: Exception) {
        null
    }

    private fun sendEmailVerificationMail(
        toEmail: String,
        token: String,
        route: String,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            sendEmail( // verification mail
                to = toEmail,
                subject = "Authentication Mail",
                content = (
                        (
                                "<html>"
                                        + "<body>"
                                        + "<h1>Email Authentication</h1>"
                                        + "<p>Click the following link to verify your email:</p>"
                                        + "<a href=\"${Constants.BASE_URL + route}?token=" + token
                                ) + "\">Authenticate</a>"
                                + "</body>"
                                + "</html>"
                        )
            )
        }
    }


    override suspend fun getLeaveBalance(type: String, email: String): GetBalanceRes {
        val teacher = teacher.getTeacher(email) ?: return GetBalanceRes()

        return leave.leaveUtils.getLeaveBalance(
            teacherId = teacher.id.value,
            type = type
        )?.let {
            GetBalanceRes(
                balance = it
            )
        } ?: GetBalanceRes()
    }

    override suspend fun handleLeaveReq(
        req: ApplyLeaveReq,
        filePath: String?,
    ): ApplyLeaveRes {
        val response = leave.applyLeave.applyLeave(
            req = req,
            doc = filePath
        )
        // send mails
        if (response.status == ApplyLeaveStatus.ACCEPTED) CoroutineScope(Dispatchers.IO).launch {
            val sendMailToTeacher = async {
                leaveAcceptanceLetter(
                    to = req.email,
                    leaveType = req.leaveType,
                    fromDate = req.fromDate,
                    toDate = req.toDate,
                    totalDays = req.totalDays,
                    reqDateTime = LocalDateTime.now().toString()
                )
            }

            val sendMailToHead = async {
                val path = query {
                    Path.find {
                        PathTable.type eq req.path
                    }.single()
                }

                val teacherDetails = query {
                    teacher.getTeacher(req.email)?.let {
                        teacher.getTeacherDetails(it.email, it.id.value)
                    }
                } ?: return@async

                val department = query {
                    Department.find {
                        DepartmentTable.id eq teacherDetails.departmentId
                    }.single()
                }

                val email = when {
                    path.type.startsWith("P") -> query { Principal.all().first().email }
                    path.type.startsWith("H") -> query { HeadClark.all().first().email }
                    else -> query {
                        val departmentHead = query {
                            DepartmentHead.find {
                                DepartmentHeadTable.departmentId eq teacherDetails.departmentId
                            }.single()
                        }

                        teacher.getTeacherOnId(departmentHead.teacherId.value).email
                    }
                }

                leaveReqNotificationToHead(
                    to = email,
                    leaveType = req.leaveType,
                    fromDate = req.fromDate,
                    toDate = req.toDate,
                    totalDays = req.totalDays,
                    reqDateTime = LocalDateTime.now().toString(),
                    department = department.name,
                    name = teacherDetails.name
                )
            }

            sendMailToTeacher.await()
            sendMailToHead.await()
        }


        return response
    }

    override suspend fun getLeaveHistory(
        email: String,
        page: Int,
        pageSize: Int,
    ): List<LeaveHistoryRes> {
        val teacher = teacher.getTeacher(email) ?: return emptyList()

        return leave.leaveUtils.getHistoryLeaves(
            teacherId = teacher.id.value,
            page = page,
            pageSize = pageSize
        )
    }

    override suspend fun getApproveLeave(
        email: String,
        page: Int,
        pageSize: Int,
    ): List<LeaveApproveRes> = coroutineScope {
        val teacher = teacher.getTeacher(email)

        if (teacher != null) { // get department id and check if department head
            val head = query {
                DepartmentHead.find {
                    DepartmentHeadTable.teacherId eq teacher.id
                }.singleOrNull()
            } ?: return@coroutineScope emptyList()

            leave.leaveUtils.getApproveLeaveAsDepartmentHead(
                departmentId = head.departmentId.value,
                teacherHeadId = teacher.id.value,
                page = page,
                pageSize = pageSize
            )
        } else {
            val principalDef = async { getPrinciple() }
            val headClarkDef = async { getHeadClark() }

            when {
                principalDef.await().email == email -> leave.leaveUtils.getApproveLeaveAsHead(
                    page = page,
                    pageSize = pageSize,
                    isPrincipal = true
                )

                headClarkDef.await().email == email -> leave.leaveUtils.getApproveLeaveAsHead(
                    page = page,
                    pageSize = pageSize,
                    isPrincipal = false
                )

                else -> emptyList()
            }
        }
    }

    override suspend fun handleLeave(
        req: HandleLeaveReq,
        email: String,
    ): Boolean = coroutineScope {
        val teacherDef = async { teacher.getTeacher(email) }
        val principalDef = async { getPrinciple() }
        val headClarkDef = async { getHeadClark() }

        val teacher = teacherDef.await()
        val principal = principalDef.await()
        val headClark = headClarkDef.await()

        val headType = when {
            principal.email == email -> HeadType.PRINCIPAL
            headClark.email == email -> HeadType.HEAD_CLARK
            teacher != null -> {
                query {
                    DepartmentHead.find {
                        DepartmentHeadTable.teacherId eq teacher.id
                    }.singleOrNull()
                } ?: return@coroutineScope false

                HeadType.HOD
            }

            else -> return@coroutineScope false
        }

        val (type, teacherId) = when (headType) {
            HeadType.PRINCIPAL -> leave.applyLeave.handleLeave(
                req = req,
                headType = HeadType.PRINCIPAL
            )

            HeadType.HEAD_CLARK -> leave.applyLeave.handleLeave(
                req = req,
                headType = HeadType.HEAD_CLARK
            )

            HeadType.HOD -> leave.applyLeave.handleLeave(
                req = req,
                headType = HeadType.HOD
            )
        }

        val reqTeacherEmail = this@ServiceRepositoryImpl.teacher.getTeacherOnId(teacherId).email

        val leaveDef = async {
            this@ServiceRepositoryImpl.leave.leaveUtils.getLeaveOnId(req.leaveId)
        }
        val detailsDef = async {
            this@ServiceRepositoryImpl.teacher.getTeacherWithDetails(reqTeacherEmail)
        }

        val details = detailsDef.await()
        val leave = leaveDef.await()

        val leaveType = query {
            LeaveType.find {
                LeaveTypeTable.id eq leave.leaveTypeId
            }.single()
        }

        CoroutineScope(Dispatchers.IO).launch {
            when (type) {
                LeaveAction.TYPE.APPROVED -> leaveUpdateLetter(
                    to = details.email,
                    content = """
                   <!DOCTYPE html>
                    <html>
                        <body>
                            <p>Hello <strong>${details.name}</strong>,</p>
                            <p>Your <strong>${leaveType.type}</strong> from <strong>${leave.fromDate}</strong> to <strong>${leave.toDate}</strong> of total <strong>${
                        ChronoUnit.DAYS.between(leave.fromDate, leave.toDate) + 1L
                    } days</strong>, has been <strong>GRANTED</strong>.</p>
                            <p>This is an auto-generated mail. Please do not reply to this message.</p>
                            <p>Regards,<br>
                            ${System.getenv("college")}</p>
                        </body>
                    </html>
                    """
                )

                LeaveAction.TYPE.FORWARD -> leaveUpdateLetter(
                    to = details.email,
                    content = """
                    <!DOCTYPE html>
                    <html>
                        <body>
                            <p>Hello <strong>${details.name}</strong>,</p>
                            <p>Your <strong>${leaveType.type}</strong> from <strong>${leave.fromDate}</strong> to <strong>${leave.toDate}</strong> of total <strong>${
                        ChronoUnit.DAYS.between(leave.fromDate, leave.toDate) + 1L
                    } days</strong>, has been <strong>FORWARDED TO PRINCIPAL</strong>.</p>
                            <p>This is an auto-generated mail. Please do not reply to this message.</p>
                            <p>Regards,<br>
                            ${System.getenv("college")}</p>
                        </body>
                    </html>
                """
                )

                LeaveAction.TYPE.REJECT -> leaveUpdateLetter(
                    to = details.email,
                    content = """
                    <!DOCTYPE html>
                    <html>
                        <body>
                            <p>Hello <strong>${details.name}</strong>,</p>
                            <p>Your <strong>${leaveType.type}</strong> from <strong>${leave.fromDate}</strong> to <strong>${leave.toDate}</strong> of total <strong>${
                        ChronoUnit.DAYS.between(leave.fromDate, leave.toDate) + 1L
                    } days</strong>, has been <strong>REJECTED BY THE ${
                        when (headType) {
                            HeadType.HOD -> "HEAD OF THE DEPARTMENT"
                            HeadType.PRINCIPAL -> "PRINCIPAL"
                            HeadType.HEAD_CLARK -> "HEAD CLARK"
                        }
                    }</strong>.</p>
                            <p>This is an auto-generated mail. Please do not reply to this message.</p>
                            <p>Regards,<br>
                            ${System.getenv("college")}</p>
                        </body>
                    </html>
                """
                )
            }
        }

        true
    }

    override suspend fun viewLeave(
        department: String,
        teacher: String,
        email: String,
        page: Int,
        pageSize: Int,
    ): List<ViewLeaveSingleRes> = coroutineScope {
        val principalDef = async { getPrinciple() }
        val headClarkDef = async { getHeadClark() }
        val teacherDef = async {
            if (teacher == "All") null else query {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.name eq teacher
                }.singleOrNull()?.let { it[TeacherDetailsTable.teacherId].value }
            }
        }

        val principal = principalDef.await()
        val headClark = headClarkDef.await()
        val teacherId = teacherDef.await() ?: -1

        when {
            principal.email == email -> {
                val id = if (department.uppercase() == "ALL") -1 else {
                    query {
                        Department.find {
                            DepartmentTable.name eq department
                        }.singleOrNull()?.id?.value
                    } ?: return@coroutineScope emptyList()
                }

                leave.leaveUtils.viewLeave(
                    dpId = id,
                    teacherId = teacherId,
                    email = email,
                    page = page,
                    pageSize = pageSize,
                    headType = HeadType.PRINCIPAL
                )
            }

            headClark.email == email -> {
                val dpId = query {
                    Department.find {
                        DepartmentTable.name eq "NTS"
                    }.first().id.value
                }

                leave.leaveUtils.viewLeave(
                    dpId = dpId,
                    teacherId = teacherId,
                    email = email,
                    page = page,
                    pageSize = pageSize,
                    headType = HeadType.HEAD_CLARK
                )
            }

            else -> {
                leave.leaveUtils.viewLeave(
                    dpId = -10,
                    teacherId = teacherId,
                    email = email,
                    page = page,
                    pageSize = pageSize,
                    headType = HeadType.HOD
                )
            }
        }
    }

    override suspend fun isStillDepartmentInCharge(email: String): Boolean = query {
        val teacher = teacher.getTeacher(email) ?: return@query false

        query {
            DepartmentHead.find {
                DepartmentHeadTable.teacherId eq teacher.id
            }.empty().not()
        }
    }

    override suspend fun getDepartmentInCharge(
        email: String,
        departmentName: String,
    ): GetDepartmentInChargeRes = coroutineScope {
        val department = query {
            Department.find {
                DepartmentTable.name.upperCase() eq departmentName.trim().uppercase()
            }.singleOrNull()
        } ?: return@coroutineScope GetDepartmentInChargeRes()

        val departmentHeadId = query {
            DepartmentHead.find {
                DepartmentHeadTable.departmentId eq department.id
            }.singleOrNull()?.teacherId?.value
        } ?: return@coroutineScope GetDepartmentInChargeRes()

        val teacherTypeId = query {
            TeacherType.find {
                TeacherTypeTable.type eq "Permenent"
            }.single().id
        }

        val allTeacher = query {
            TeacherDetailsTable
                .slice(
                    TeacherDetailsTable.teacherId,
                    TeacherDetailsTable.name
                )
                .select {
                    TeacherDetailsTable.departmentId eq department.id and
                            (TeacherDetailsTable.teacherTypeId eq teacherTypeId)
                }.map {
                    DepartmentTeacher(
                        id = it[TeacherDetailsTable.teacherId].value,
                        name = it[TeacherDetailsTable.name]
                    )
                }
        }

        val currentHead = allTeacher.first {
            it.id == departmentHeadId
        }

        val newList = allTeacher.filterNot {
            it.id == departmentHeadId
        }

        GetDepartmentInChargeRes(
            current = currentHead.name,
            others = newList
        )
    }

    override suspend fun addTeacher(req: AddTeacherReq): Boolean {
        if (!isValidEmail(req.email)) return false

        val status = teacher.addTeacher(req.email)

        if (status) {
            CoroutineScope(Dispatchers.IO).launch {
                sendEmail(
                    to = req.email,
                    subject = "You can now logIn in our Leave Management System app",
                    content = """
                        Your given email has been added to our database.
                        You can now Login through our app and manager all your leaves.
                        
                        This is an auto-generated mail. Please do not reply to this message.
            
                        Regards,
                        ${System.getenv("college")}
                    """
                )
            }
        }

        return status
    }

    override suspend fun updateDepartmentHead(
        teacher: String,
        department: String,
    ): Boolean {
        val oldDepartmentHeadTeacher = this.teacher.getDepartmentHead(department) ?: return false
        val newDepartmentHeadTeacher = query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.name eq teacher
            }.singleOrNull()?.let {
                it[TeacherDetailsTable.teacherId].value
            }?.let {
                Teacher.find {
                    TeacherTable.id eq it
                }.singleOrNull()
            }
        } ?: return false

        val result = this.teacher.updateDepartmentHead(
            teacher = teacher,
            department = department
        )

        if (result) CoroutineScope(Dispatchers.IO).launch {
            val oldDef = async {
                deleteCookie(oldDepartmentHeadTeacher.email)
            }

            val newDef = async {
                deleteCookie(newDepartmentHeadTeacher.email)
            }

            oldDef.await()
            newDef.await()

            val sendmailToOldDef = async {
                sendEmail(
                    to = oldDepartmentHeadTeacher.email,
                    subject = "You are demoted from Department Head",
                    content = """
                        Please logout and and login from the app.
                        
                        This is an auto-generated mail. Please do not reply to this message.
            
                        Regards,
                        ${System.getenv("college")}
                    """
                )
            }

            val sendmailToNewDef = async {
                sendEmail(
                    to = newDepartmentHeadTeacher.email,
                    subject = "You have been promoted as Department Head",
                    content = """
                        Please logout and and login from the app.
                        
                        This is an auto-generated mail. Please do not reply to this message.
            
                        Regards,
                        ${System.getenv("college")}
                    """
                )
            }

            sendmailToOldDef.await()
            sendmailToNewDef.await()
        }

        return result
    }


    override suspend fun getDepartmentTeacher(department: String): GetDepartmentTeacher {
        val dep = query {
            Department.find {
                DepartmentTable.name eq department
            }.firstOrNull()
        } ?: return GetDepartmentTeacher()

        return GetDepartmentTeacher(
            departmentId = dep.id.value,
            teacherName = query {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.departmentId eq dep.id
                }.map {
                    Teacher(
                        id = it[TeacherDetailsTable.teacherId].value,
                        name = it[TeacherDetailsTable.name]
                    )
                }
            }
        )
    }

    override suspend fun getTeacherLeaveBalance(teacherId: Int): List<TeacherLeaveBalance> =
        teacher.getTeacherLeaveBalance(teacherId)

    override suspend fun updateLeaveBalance(req: UpdateLeaveBalanceReq): Boolean = teacher.updateLeaveBalance(req)

    override suspend fun getReport(
        department: String,
        type: String,
        teacher: String,
    ): List<ReportResponse> {
        return if (department.uppercase() == "ALL") getReportForAllDepartment(type)
        else getReportForOneDepartment(
            department = department,
            type = type,
            teacher = teacher
        )
    }

    override suspend fun generatePdfData(
        department: String,
        type: String,
        teacher: String,
    ): List<PdfData> {
        return if (department.uppercase() == "ALL") {
            query {
                LeaveReqTable
                    .join(
                        otherTable = LeaveStatusTable,
                        joinType = JoinType.INNER,
                        additionalConstraint = {
                            LeaveStatusTable.leaveId eq LeaveReqTable.id
                        }
                    )
                    .join(
                        otherTable = DepartmentTable,
                        joinType = JoinType.INNER,
                        additionalConstraint = {
                            DepartmentTable.id eq LeaveStatusTable.departmentId
                        }
                    )
                    .join(
                        otherTable = LeaveTypeTable,
                        joinType = JoinType.INNER,
                        additionalConstraint = {
                            LeaveTypeTable.id eq LeaveReqTable.leaveTypeId
                        }
                    )
                    .join(
                        otherTable = TeacherDetailsTable,
                        joinType = JoinType.INNER,
                        additionalConstraint = {
                            TeacherDetailsTable.teacherId eq LeaveReqTable.teacherId
                        }
                    )
                    .slice(
                        LeaveReqTable.id,
                        LeaveTypeTable.type,
                        DepartmentTable.name,
                        TeacherDetailsTable.name,
                        LeaveReqTable.reqDate,
                        LeaveReqTable.fromDate,
                        LeaveReqTable.toDate,
                        LeaveReqTable.reason
                    ).let {
                        query {
                            if (type.uppercase() == "ALL") it.select {
                                LeaveStatusTable.statusId neq PENDING_STATUS_CODE
                            }
                            else {
                                val leaveId = LeaveType.find {
                                    LeaveTypeTable.type eq type.split('(')[0]
                                }.first().id.value

                                it.select {
                                    LeaveTypeTable.id eq leaveId and (LeaveStatusTable.statusId neq PENDING_STATUS_CODE)
                                }
                            }
                        }
                    }.orderBy(DepartmentTable.name)
                    .orderBy(TeacherDetailsTable.name)
                    .map {
                        ReportResult(
                            leaveId = it[LeaveReqTable.id].value,
                            departmentName = it[DepartmentTable.name],
                            teacherName = it[TeacherDetailsTable.name],
                            leaveType = it[LeaveTypeTable.type],
                            reqDate = it[LeaveReqTable.reqDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                            fromDate = it[LeaveReqTable.fromDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                            toDate = it[LeaveReqTable.toDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                            totalDays = (ChronoUnit.DAYS.between(
                                it[LeaveReqTable.fromDate],
                                it[LeaveReqTable.toDate]
                            ) + 1).toInt(),
                            reason = it[LeaveReqTable.reason]
                        )
                    }.groupBy { it.departmentName }.map { map ->
                        val report = map.value.map { res ->
                            PdfReportData(
                                applicationDate = res.reqDate,
                                leaveType = res.leaveType,
                                fromDate = res.fromDate,
                                toDate = res.toDate,
                                totalDays = res.totalDays.toString(),
                                status = res.reason
                            )
                        }

                        PdfData(
                            department = map.key,
                            listOfData = map.value.groupBy { it.teacherName }.map { result ->
                                PdfTeacher(
                                    name = result.key,
                                    designation = getTeacherDesignationOnTeacherName(result.key),
                                    listOfData = report
                                )
                            }
                        )
                    }
            }
        } else {
            coroutineScope {
                val teacherIdDef = async {
                    if (teacher == "All") null else query {
                        TeacherDetailsTable.select {
                            TeacherDetailsTable.name eq teacher
                        }.singleOrNull()?.let { it[TeacherDetailsTable.teacherId].value }
                    }
                }

                val leaveTypeIdDef = async {
                    if (type.uppercase() == "ALL") {
                        null
                    } else query {
                        LeaveType.find {
                            LeaveTypeTable.type eq type.split('(')[0]
                        }.single().id.value
                    }
                }

                val depDef = async {
                    query {
                        Department.find {
                            DepartmentTable.name eq department
                        }.single()
                    }
                }

                val teacherId = teacherIdDef.await()
                val leaveTypeId = leaveTypeIdDef.await()
                val dep = depDef.await()

                val leaveIdList = query {
                    LeaveStatusTable.select {
                        LeaveStatusTable.departmentId eq dep.id and (LeaveStatusTable.statusId neq PENDING_STATUS_CODE)
                    }.map {
                        it[LeaveStatusTable.leaveId].value
                    }
                }

                query {
                    val response = if (leaveTypeId != null) {
                        if (teacherId != null) LeaveReq.find {
                            LeaveReqTable.id inList leaveIdList and (LeaveReqTable.leaveTypeId eq leaveTypeId) and (LeaveReqTable.teacherId eq teacherId)
                        } else LeaveReq.find {
                            LeaveReqTable.id inList leaveIdList and (LeaveReqTable.leaveTypeId eq leaveTypeId)
                        }
                    } else {
                        if (teacherId != null) LeaveReq.find {
                            LeaveReqTable.id inList leaveIdList and (LeaveReqTable.teacherId eq teacherId)
                        } else LeaveReq.find {
                            LeaveReqTable.id inList leaveIdList
                        }
                    }

                    response.groupBy {
                        it.teacherId
                    }.map {
                        async {
                            query {
                                TeacherDetailsTable.select {
                                    TeacherDetailsTable.teacherId eq it.key
                                }.single().let { resultRow ->
                                    resultRow[TeacherDetailsTable.name]
                                }.let {
                                    Pair(
                                        first = it,
                                        second = getTeacherDesignationOnTeacherName(it)
                                    )
                                }
                            } to it.value.map { leaveReq ->
                                PdfReportData(
                                    applicationDate = leaveReq.reqDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                                    leaveType = query {
                                        LeaveType.find {
                                            LeaveTypeTable.id eq leaveReq.leaveTypeId
                                        }.first().type
                                    },
                                    fromDate = leaveReq.fromDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                                    toDate = leaveReq.toDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                                    totalDays = (ChronoUnit.DAYS.between(
                                        leaveReq.fromDate,
                                        leaveReq.toDate
                                    ) + 1).toString(),
                                    status = leaveReq.reason
                                )
                            }
                        }
                    }.awaitAll().map {
                        PdfTeacher(
                            name = it.first.first,
                            designation = it.first.second,
                            listOfData = it.second
                        )
                    }.let {
                        listOf(
                            PdfData(
                                department = department,
                                listOfData = it
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun getTeacherDesignationOnTeacherName(name: String) = query {
        TeacherDetailsTable.slice(TeacherDetailsTable.designationId).select {
            TeacherDetailsTable.name eq name
        }.single().let { it[TeacherDetailsTable.designationId].value }.let {
            query {
                Designation.find {
                    DesignationTable.id eq it
                }.single().type
            }
        }
    }

    override suspend fun getTeacherToDelete(department: String): List<ResponseTeacher> {
        val dep = query {
            Department.find {
                DepartmentTable.name eq department
            }.single().id.value
        }

        val depHeadId = query {
            DepartmentHead.find {
                DepartmentHeadTable.departmentId eq dep
            }.singleOrNull()?.teacherId?.value
        }

        return query {
            TeacherDetailsTable
                .join(
                    otherTable = DesignationTable,
                    joinType = JoinType.INNER,
                    additionalConstraint = {
                        TeacherDetailsTable.designationId as Column<*> eq DesignationTable.id
                    }
                ).slice(
                    TeacherDetailsTable.teacherId,
                    TeacherDetailsTable.name,
                    TeacherDetailsTable.profilePic,
                    DesignationTable.type,
                )
                .select {
                    if (depHeadId != null) TeacherDetailsTable.departmentId eq dep and (TeacherDetailsTable.teacherId neq depHeadId)
                    else TeacherDetailsTable.departmentId eq dep
                }.map {
                    ResponseTeacher(
                        id = it[TeacherDetailsTable.teacherId].value,
                        name = it[TeacherDetailsTable.name],
                        designation = it[DesignationTable.type],
                        profile = "${Constants.BASE_URL + EndPoints.GetImage.route}?profile=${it[TeacherDetailsTable.profilePic]}"
                    )
                }
        }
    }

    override suspend fun deleteTeacher(req: DeleteTeacherReq): Boolean = teacher.deleteTeacher(req.id)

    private suspend fun getReportForOneDepartment(
        department: String,
        type: String,
        teacher: String,
    ) = coroutineScope {
        val teacherIdDef = async {
            if (teacher == "All") null else query {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.name eq teacher
                }.singleOrNull()?.let { it[TeacherDetailsTable.teacherId].value }
            }
        }

        val leaveTypeIdDef = async {
            if (type.uppercase() == "ALL") null
            else query {
                LeaveType.find {
                    LeaveTypeTable.type eq type.split('(')[0]
                }.single().id.value
            }
        }

        val depDef = async {
            query {
                Department.find {
                    DepartmentTable.name eq department
                }.single()
            }
        }

        val teacherId = teacherIdDef.await()
        val leaveTypeId = leaveTypeIdDef.await()
        val dep = depDef.await()

        val leaveIdList = query {
            LeaveStatusTable.select {
                LeaveStatusTable.departmentId eq dep.id and (LeaveStatusTable.statusId neq PENDING_STATUS_CODE)
            }.map {
                it[LeaveStatusTable.leaveId].value
            }
        }

        query {
            val response = if (leaveTypeId != null) {
                if (teacherId != null) LeaveReq.find {
                    LeaveReqTable.id inList leaveIdList and (LeaveReqTable.leaveTypeId eq leaveTypeId) and (LeaveReqTable.teacherId eq teacherId)
                } else LeaveReq.find {
                    LeaveReqTable.id inList leaveIdList and (LeaveReqTable.leaveTypeId eq leaveTypeId)
                }
            } else {
                if (teacherId != null) LeaveReq.find {
                    LeaveReqTable.id inList leaveIdList and (LeaveReqTable.teacherId eq teacherId)
                } else LeaveReq.find {
                    LeaveReqTable.id inList leaveIdList
                }
            }

            response.groupBy {
                it.teacherId
            }.map {
                async {
                    query {
                        TeacherDetailsTable.select {
                            TeacherDetailsTable.teacherId eq it.key
                        }.singleOrNull()?.let { resultRow ->
                            resultRow[TeacherDetailsTable.name]
                        } ?: ""
                    }
                } to it.value.map { leaveReq ->
                    LeaveData(
                        applicationDate = leaveReq.reqDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                        reqType = query {
                            LeaveType.find {
                                LeaveTypeTable.id eq leaveReq.leaveTypeId
                            }.first().type
                        },
                        fromDate = leaveReq.fromDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                        toDate = leaveReq.toDate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                        totalDays = (ChronoUnit.DAYS.between(
                            leaveReq.fromDate,
                            leaveReq.toDate
                        ) + 1).toString()
                    )
                }
            }.map {
                ReportResponse(
                    department = department,
                    name = it.first.await(),
                    listOfLeave = it.second
                )
            }
        }
    }

    private suspend fun getReportForAllDepartment(
        type: String,
    ) = query {
        val join = LeaveReqTable
            .join(
                otherTable = LeaveStatusTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    LeaveStatusTable.leaveId eq LeaveReqTable.id
                }
            )
            .join(
                otherTable = DepartmentTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    DepartmentTable.id eq LeaveStatusTable.departmentId
                }
            )
            .join(
                otherTable = LeaveTypeTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    LeaveTypeTable.id eq LeaveReqTable.leaveTypeId
                }
            )
            .join(
                otherTable = TeacherDetailsTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    TeacherDetailsTable.teacherId eq LeaveReqTable.teacherId
                }
            )
            .slice(
                LeaveReqTable.id,
                LeaveTypeTable.type,
                DepartmentTable.name,
                TeacherDetailsTable.name,
                LeaveReqTable.reqDate,
                LeaveReqTable.fromDate,
                LeaveReqTable.toDate,
                LeaveReqTable.reason,
                LeaveStatusTable.statusId
            )


        (if (type.uppercase() == "ALL") join.select {
            LeaveStatusTable.statusId neq PENDING_STATUS_CODE
        }
        else {
            val leaveId = LeaveType.find {
                LeaveTypeTable.type eq type.split('(')[0]
            }.first().id.value

            join.select {
                LeaveTypeTable.id eq leaveId and (LeaveStatusTable.statusId neq PENDING_STATUS_CODE)
            }
        }).orderBy(DepartmentTable.name)
            .orderBy(TeacherDetailsTable.name)
            .map {
                ReportResult(
                    leaveId = it[LeaveReqTable.id].value,
                    departmentName = it[DepartmentTable.name],
                    teacherName = it[TeacherDetailsTable.name],
                    leaveType = it[LeaveTypeTable.type],
                    reqDate = it[LeaveReqTable.reqDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                    fromDate = it[LeaveReqTable.fromDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                    toDate = it[LeaveReqTable.toDate].format(DateTimeFormatter.ofPattern("yyyy-dd-MM")),
                    totalDays = (ChronoUnit.DAYS.between(
                        it[LeaveReqTable.fromDate],
                        it[LeaveReqTable.toDate]
                    ) + 1).toInt(),
                    reason = it[LeaveReqTable.reason]
                )
            }.groupBy { it.teacherName }
            .map {
                ReportResponse(
                    department = it.value.first().departmentName,
                    name = it.key,
                    listOfLeave = it.value.map { leave ->
                        LeaveData(
                            reqType = leave.leaveType,
                            applicationDate = leave.reqDate,
                            fromDate = leave.fromDate,
                            toDate = leave.toDate,
                            totalDays = leave.totalDays.toString()
                        )
                    }
                )
            }
    }

    private suspend fun deleteCookie(oldEmail: String) = query {
        val entrys = SessionStorageDB.find {
            SessionStorageTable.value like "%${oldEmail.replace("@gmail.com", "")}%"
        }.toList()

        entrys.map {
            val email = it.value.split("&")[0].substringAfter("email=").let { s ->
                URLDecoder.decode(s, StandardCharsets.UTF_8.name)
            }.removePrefix("#s")

            if (email == oldEmail) {
                it.delete()
            }
        }
    }

    private fun isValidEmail(email: String) =
        email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"))

    private fun SetDetailsReq.validateDetails(): Boolean {
        if (this.email.isBlank() ||
            this.name.isBlank() ||
            this.dbo.isBlank() ||
            this.exp.isBlank()
        ) return false

        if (this.sex.uppercaseChar() !in listOf('M', 'F', 'O')) return false

        if (phone_1.length != 10) return false

        if (phone_2.isNotBlank()) if (phone_2.length != 10) return false

        if (department.isBlank() ||
            hrmsId.isBlank() ||
            designation.isBlank() ||
            joiningDate.isBlank()
        ) return false

        val areAddressesValid = this.address.all {
            it.second.houseNumber.isNotBlank() &&
                    it.second.street.isNotBlank() &&
                    it.second.city.isNotBlank() &&
                    it.second.zipcode.isNotBlank() &&
                    it.second.state.isNotBlank() &&
                    it.second.country.isNotBlank()
        }

        return areAddressesValid
    }


    private fun leaveAcceptanceLetter(
        to: String,
        leaveType: String,
        fromDate: String,
        toDate: String,
        totalDays: String,
        reqDateTime: String,
    ) {
        val subject = "Leave Request Accepted"
        val messageContent = """
        <!DOCTYPE html>
        <html>
            <body>
                <p>Your leave request for <strong>$leaveType</strong> from <strong>$fromDate</strong> to <strong>$toDate</strong>, a total of <strong>$totalDays</strong> days, has been accepted.</p>
                <p>Request submitted on: <strong>$reqDateTime</strong>.</p>
                <p>This is an auto-generated mail. Please do not reply to this message.</p>
                <p>Regards,<br>
                ${System.getenv("college")}</p>
            </body>
        </html>
        """
        sendEmail(
            to = to,
            subject = subject,
            content = messageContent
        )
    }

    private fun leaveReqNotificationToHead(
        to: String,
        leaveType: String,
        fromDate: String,
        toDate: String,
        totalDays: String,
        reqDateTime: String,
        department: String,
        name: String,
    ) {
        val subject = "A Leave Request is made by $name"
        val messageContent = """
        <!DOCTYPE html>
        <html>
            <body>
                <p>A leave request is made by <strong>$name</strong> from the <strong>$department</strong> department for <strong>$leaveType</strong> from <strong>$fromDate</strong> to <strong>$toDate</strong>, a total of <strong>$totalDays</strong> days.</p>
                <p>Request submitted on: <strong>$reqDateTime</strong>.</p>
                <p>This is an auto-generated mail. Please do not reply to this message.</p>
                <p>Regards,<br>
                ${System.getenv("college")}</p>
            </body>
        </html>
        """

        sendEmail(
            to = to,
            subject = subject,
            content = messageContent
        )
    }

    private fun leaveUpdateLetter(
        to: String,
        subject: String = "Leave Request Update.",
        content: String,
    ) {
        sendEmail(
            to = to,
            subject = subject,
            content = content
        )
    }

    private suspend fun getPrinciple() = query {
        Principal.all().single()
    }

    private suspend fun getHeadClark() = query { HeadClark.all().first() }
}