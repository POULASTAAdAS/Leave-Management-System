package com.poulastaa.domain.repository

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.details.UpdateHeadDetailsReq
import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.designation.DesignationTeacherTypeRelation
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.model.table.utils.PathTable
import com.poulastaa.data.repository.JWTRepository
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.data.repository.leave.LeaveWrapper
import com.poulastaa.domain.dao.department.Department
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.leave.LeaveAction
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.utils.HeadClark
import com.poulastaa.domain.dao.utils.Path
import com.poulastaa.domain.dao.utils.Principal
import com.poulastaa.invalidTokenList
import com.poulastaa.plugins.dbQuery
import com.poulastaa.utils.Constants.LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.Constants.SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.sendEmail
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.select
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ServiceRepositoryImpl(
    private val jwtRepo: JWTRepository,
    private val teacher: TeacherRepository,
    private val leave: LeaveWrapper
) : ServiceRepository {
    override suspend fun auth(email: String): AuthRes {
        if (!validateEmail(email)) return AuthRes()

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
                        profilePicUrl = System.getenv("BASE_URL") + EndPoints.GetProfilePic.route
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

            else -> AuthRes()
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
        req: UpdateDetailsReq
    ): Boolean {
        if (req.email.isNotEmpty() && !validateEmail(req.email)) return false

        return teacher.updateDetails(email, req)
    }

    override suspend fun updateHeadDetails(email: String, req: UpdateHeadDetailsReq): Boolean {
        if (req.email.isNotEmpty() && !validateEmail(req.email)) return false

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
        teacher.getProfilePic(email)?.let { File("${System.getenv("profileFolder")}$it") }
    } catch (e: Exception) {
        null
    }

    private fun sendEmailVerificationMail(
        toEmail: String,
        token: String,
        route: String
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
                                        + "<a href=\"${System.getenv("BASE_URL") + route}?token=" + token
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
        filePath: String?
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
                val path = dbQuery {
                    Path.find {
                        PathTable.type eq req.path
                    }.single()
                }

                val teacherDetails = dbQuery {
                    teacher.getTeacher(req.email)?.let {
                        teacher.getTeacherDetails(it.email, it.id.value)
                    }
                } ?: return@async

                val departmentHead = dbQuery {
                    DepartmentHead.find {
                        DepartmentHeadTable.departmentId eq teacherDetails.departmentId
                    }.single()
                }

                val department = dbQuery {
                    Department.find {
                        DepartmentTable.id eq departmentHead.departmentId
                    }.single()
                }

                val email = when {
                    path.type.startsWith("P") -> dbQuery { Principal.all().first().email }
                    path.type.startsWith("H") -> dbQuery { HeadClark.all().first().email }
                    else -> dbQuery { teacher.getTeacherOnId(departmentHead.teacherId.value).email }
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
        pageSize: Int
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
        pageSize: Int
    ): List<LeaveApproveRes> = coroutineScope {
        val teacher = teacher.getTeacher(email)

        if (teacher != null) {
            // get department id and check if department head
            val head = dbQuery {
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
            val principal = getPrinciple()
            if (principal.email != email) return@coroutineScope emptyList()

            leave.leaveUtils.getApproveLeaveAsHead(
                page = page,
                pageSize = pageSize
            )
        }
    }

    override suspend fun handleLeave(req: HandleLeaveReq, email: String): Boolean = coroutineScope {
        val teacher = teacher.getTeacher(email)
        val principal = getPrinciple()

        val isPrincipal = email == principal.email

        val (type, teacherId) = if (isPrincipal) {
            leave.applyLeave.handleLeave(
                req = req,
                isPrincipal = true
            )
        } else {
            if (teacher == null) return@coroutineScope false

            dbQuery {
                DepartmentHead.find {
                    DepartmentHeadTable.teacherId eq teacher.id
                }.singleOrNull()
            } ?: return@coroutineScope false

            leave.applyLeave.handleLeave(
                req = req,
                isPrincipal = false
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

        val leaveType = dbQuery {
            LeaveType.find {
                LeaveTypeTable.id eq leave.leaveTypeId
            }.single()
        }

        when (type) {
            LeaveAction.TYPE.APPROVED -> leaveUpdateLetter(
                to = details.email,
                subject = "Leave Request Update.",
                content = """
                    Hello ${details.name}
                    Your ${leaveType.type} from ${leave.fromDate} to ${leave.toDate} of total ${
                    ChronoUnit.DAYS.between(
                        leave.fromDate,
                        leave.toDate
                    ) + 1L
                } days,
                    has been GRANTED.
                    
                    This is an auto-generated mail. Please do not reply to this message.
            
                    Regards,
                    ${System.getenv("college")}
                """
            )

            LeaveAction.TYPE.FORWARD -> leaveUpdateLetter(
                to = details.email,
                subject = "Leave Request Update.",
                content = """
                    Hello ${details.name}
                    Your ${leaveType.type} from ${leave.fromDate} to ${leave.toDate} of total ${
                    ChronoUnit.DAYS.between(
                        leave.fromDate,
                        leave.toDate
                    ) + 1L
                } days,
                    has been FORWARDED TO PRINCIPLE.
                    
                    This is an auto-generated mail. Please do not reply to this message.
            
                    Regards,
                    ${System.getenv("college")}
                """
            )

            LeaveAction.TYPE.REJECT -> leaveUpdateLetter(
                to = details.email,
                subject = "Leave Request Update.",
                content = """
                    Hello ${details.name}
                    Your ${leaveType.type} from ${leave.fromDate} to ${leave.toDate} of total ${
                    ChronoUnit.DAYS.between(
                        leave.fromDate,
                        leave.toDate
                    ) + 1L
                } days,
                    has been REJECTED BY ${if (isPrincipal) "PRINCIPLE" else "DEPARTMENT HEAD"}.
                    
                    
                    This is an auto-generated mail. Please do not reply to this message.
            
                    Regards,
                    ${System.getenv("college")}
                """
            )
        }

        true
    }

    override suspend fun viewLeave(
        email: String,
        page: Int,
        pageSize: Int,
    ): List<ViewLeaveSingleRes> = coroutineScope {
        val principal = getPrinciple()


        if (principal.email == email) {
            leave.leaveUtils.viewLeave(
                email = email,
                page = page,
                pageSize = pageSize,
                isPrinciple = true
            )
        } else {
            leave.leaveUtils.viewLeave(
                email = email,
                page = page,
                pageSize = pageSize,
                isPrinciple = false
            )
        }
    }

    private fun validateEmail(email: String) =
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
        reqDateTime: String
    ) {
        val subject = "Leave Request Accepted"
        val messageContent = """
            Your leave request for $leaveType from $fromDate to $toDate,A total days of $totalDays days, has been accepted.
            Request submitted on: $reqDateTime.
            
            This is an auto-generated mail. Please do not reply to this message.
            
            Regards,
            ${System.getenv("college")}
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
        name: String
    ) {
        val subject = "A Leave Request is made by $name"
        val messageContent = """
            A leave request is made by $name from Department $department of $leaveType from $fromDate to $toDate,A total days of $totalDays days.
            Request submitted on: $reqDateTime.
            
            This is an auto-generated mail. Please do not reply to this message.
            
            Regards,
            ${System.getenv("college")}
        """

        sendEmail(
            to = to,
            subject = subject,
            content = messageContent
        )
    }

    private fun leaveUpdateLetter(
        to: String,
        subject: String,
        content: String
    ) {
        sendEmail(
            to = to,
            subject = subject,
            content = content
        )
    }

    private suspend fun getPrinciple() = dbQuery {
        Principal.all().single()
    }
}