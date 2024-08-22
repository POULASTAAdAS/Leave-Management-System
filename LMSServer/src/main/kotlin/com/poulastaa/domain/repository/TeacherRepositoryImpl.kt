package com.poulastaa.domain.repository

import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.TeacherDetails
import com.poulastaa.data.model.auth.req.ReqAddress
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.constants.AddressType
import com.poulastaa.data.model.convertors.AddressEntry
import com.poulastaa.data.model.convertors.SetDetailsEntry
import com.poulastaa.data.model.convertors.TeacherDetailsEntry
import com.poulastaa.data.model.details.TeacherAddress
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.details.UpdateHeadDetailsReq
import com.poulastaa.data.model.other.TeacherLeaveBalance
import com.poulastaa.data.model.other.UpdateLeaveBalanceReq
import com.poulastaa.data.model.table.address.AddressTypeTable
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.designation.DesignationTable
import com.poulastaa.data.model.table.designation.DesignationTeacherTypeRelation
import com.poulastaa.data.model.table.leave.LeaveBalanceTable
import com.poulastaa.data.model.table.leave.LeaveReqTable
import com.poulastaa.data.model.table.leave.LeaveTypeTable
import com.poulastaa.data.model.table.teacher.TeacherAddressTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import com.poulastaa.data.model.table.utils.HeadClarkTable
import com.poulastaa.data.model.table.utils.LogInEmailTable
import com.poulastaa.data.model.table.utils.PrincipalTable
import com.poulastaa.data.model.table.utils.QualificationTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.domain.dao.department.Department
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.leave.LeaveReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.domain.dao.teacher.TeacherType
import com.poulastaa.domain.dao.utils.*
import com.poulastaa.plugins.query
import com.poulastaa.utils.Constants.VERIFICATION_MAIL_TOKEN_TIME
import com.poulastaa.utils.toLocalDate
import com.poulastaa.utils.toTeacherAddress
import com.poulastaa.utils.toTeacherDetails
import kotlinx.coroutines.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.time.LocalDate

class TeacherRepositoryImpl : TeacherRepository {
    private suspend fun findTeacher(email: String) = query {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()
    }

    private suspend fun getPrincipal() = query {
        Principal.all().first()
    }

    private suspend fun getHeadClark() = query {
        HeadClark.all().first()
    }

    private suspend fun findPrinciple(email: String) = query {
        Principal.find {
            PrincipalTable.email eq email
        }.firstOrNull()
    }

    private suspend fun findHeadClark(email: String) = query {
        HeadClark.find {
            HeadClarkTable.email eq email
        }.singleOrNull()
    }

    override suspend fun getTeacher(email: String): Teacher? = findTeacher(email)

    override suspend fun getTeacherOnId(id: Int): Teacher = query {
        Teacher.find {
            TeacherTable.id eq id
        }.single()
    }

    override suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, Any> = coroutineScope {
        val teacherDef = async { findTeacher(email) }
        val principleDef = async { findPrinciple(email) }
        val headClarkDef = async { findHeadClark(email) }

        val teacher = teacherDef.await()
        val principal = principleDef.await()
        val headClark = headClarkDef.await()

        if (principal != null) {
            handleLoginEntry(email) // using loginIn entry to verify principle
            return@coroutineScope AuthStatus.PRINCIPLE_FOUND to principal
        }

        if (headClark != null) {
            handleLoginEntry(email)
            return@coroutineScope AuthStatus.HEAD_CLARK_FOUND to headClark
        }

        if (teacher == null) return@coroutineScope AuthStatus.EMAIL_NOT_REGISTERED to Unit

        val response = query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()
        }

        val authStatus = if (response == null) AuthStatus.SIGNUP else AuthStatus.LOGIN

        if (authStatus == AuthStatus.LOGIN) {
            handleLoginEntry(email)
        }

        authStatus to Unit
    }

    override suspend fun getTeacherWithDetails(email: String): User = withContext(Dispatchers.IO) {
        val teacher = findTeacher(email) ?: return@withContext User()

        val (designationId, departmentId) = query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()?.let {
                Pair(
                    first = it[TeacherDetailsTable.designationId].value,
                    second = it[TeacherDetailsTable.departmentId].value
                )
            }
        } ?: return@withContext User()


        val departmentDef = async {
            query {
                Department.find {
                    DepartmentTable.id eq departmentId
                }.singleOrNull()?.name
            }
        }

        val designationDef = async {
            query {
                Designation.find {
                    DesignationTable.id eq designationId
                }.singleOrNull()
            }
        }

        val isDepartmentInChargeDef = async {
            query {
                DepartmentHead.find {
                    DepartmentHeadTable.teacherId eq teacher.id
                }.empty()
            }
        }

        val department = departmentDef.await() ?: return@withContext User()
        val designation = designationDef.await() ?: return@withContext User()
        val isDepartmentHead = !isDepartmentInChargeDef.await()

        query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()?.let {
                User(
                    name = it[TeacherDetailsTable.name],
                    email = email,
                    profilePicUrl = it[TeacherDetailsTable.profilePic] ?: "",
                    department = department,
                    designation = designation.type,
                    isDepartmentInCharge = isDepartmentHead
                )
            }
        } ?: User()
    }

    override suspend fun updateSignUpVerificationStatus(email: String): VerifiedMailStatus {
        return try {
            val user = findTeacher(email) ?: return VerifiedMailStatus.USER_NOT_FOUND
            if (!user.emailVerified) query {
                user.emailVerified = true
            }

            VerifiedMailStatus.VERIFIED
        } catch (_: Exception) {
            VerifiedMailStatus.SOMETHING_WENT_WRONG
        }
    }

    override suspend fun updateLogInVerificationStatus(email: String): Pair<VerifiedMailStatus, Pair<String, String>> {
        return try {
            val user = query {
                LogInEmail.find {
                    LogInEmailTable.email eq email
                }.singleOrNull()
            } ?: return VerifiedMailStatus.USER_NOT_FOUND to Pair("", "")

            if (!user.emailVerified) query {
                user.emailVerified = true
            }

            val teacher = findTeacher(email) ?: return VerifiedMailStatus.USER_NOT_FOUND to Pair("", "")

            val entry = query {
                TeacherDetailsTable.select {
                    TeacherDetailsTable.teacherId eq teacher.id
                }.singleOrNull()?.let {
                    Pair(
                        first = it[TeacherDetailsTable.name],
                        second = email
                    )
                }
            } ?: return VerifiedMailStatus.USER_NOT_FOUND to Pair("", "")

            VerifiedMailStatus.VERIFIED to entry
        } catch (_: Exception) {
            VerifiedMailStatus.SOMETHING_WENT_WRONG to Pair("", "")
        }
    }

    override suspend fun signupEmailVerificationCheck(email: String): Boolean = query {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()?.emailVerified ?: false
    }


    override suspend fun loginEmailVerificationCheck(email: String): Boolean = query {
        val response = LogInEmail.find {
            LogInEmailTable.email eq email
        }.singleOrNull()?.emailVerified ?: false

        return@query response
    }

    override suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes = withContext(Dispatchers.IO) {
        val teacher = findTeacher(email = req.email) ?: return@withContext SetDetailsRes()
        if (!teacher.emailVerified) return@withContext SetDetailsRes()

        val dataDef = async { req.toDetailsEntry(teacher.id) }
        val isEntryEmptyDef = async { checkIfDetailsAlreadyFilled(teacher.id.value) }
        val isDepartmentHeadDef = async { getDepartmentHead(teacher.id.value) }


        val data = dataDef.await() ?: return@withContext SetDetailsRes()
        val isEmpty = isEntryEmptyDef.await()
        val isDepartmentHead = isDepartmentHeadDef.await()

        if (!isEmpty) return@withContext SetDetailsRes(
            status = TeacherDetailsSaveStatus.ALREADY_SAVED,
            isDepartmentHead = isDepartmentHead
        )

        val one = async { data.storeDetails() }
        val two = async {
            setLeaveBalance(
                teacherTypeId = data.details.teacherTypeId,
                teacherId = teacher.id.value,
                gender = req.sex
            )
        }

        one.await()
        two.await()

        SetDetailsRes(
            status = TeacherDetailsSaveStatus.SAVED,
            isDepartmentHead = isDepartmentHead
        )
    }

    override suspend fun getTeacherDetailsRes(email: String): GetTeacherRes? = coroutineScope {
        val teacher = findTeacher(email) ?: return@coroutineScope null

        val teacherDetails = query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()?.let {
                TeacherDetailsEntry(
                    email = email,
                    teacherId = teacher.id,
                    teacherTypeId = it[TeacherDetailsTable.teacherTypeId].value,
                    hrmsId = "",
                    name = it[TeacherDetailsTable.name],
                    phone_1 = it[TeacherDetailsTable.phone_1],
                    phone_2 = it[TeacherDetailsTable.phone_2],
                    bDate = it[TeacherDetailsTable.bDate],
                    gender = it[TeacherDetailsTable.gender],
                    designationId = it[TeacherDetailsTable.designationId],
                    departmentId = it[TeacherDetailsTable.departmentId],
                    joiningDate = it[TeacherDetailsTable.joiningDate],
                    qualificationId = it[TeacherDetailsTable.qualificationId],
                    exp = it[TeacherDetailsTable.exp]
                )
            }
        } ?: return@coroutineScope null

        val departmentDef = async { getDepartmentOnId(teacherDetails.departmentId.value) }
        val designationDef = async { getDesignationOnId(teacherDetails.designationId.value) }
        val qualificationDef = async { getQualification(teacherDetails.qualificationId.value) }

        val addressDef = async { getAddressOnTeacherId(teacher.id.value) }

        val department = departmentDef.await()
        val designation = designationDef.await()
        val qualification = qualificationDef.await()
        val address = addressDef.await()

        GetTeacherRes(
            name = teacherDetails.name,
            profilePicUrl = teacherDetails.profilePic ?: "",
            gender = teacherDetails.gender,
            email = email,
            phoneOne = teacherDetails.phone_1,
            phoneTwo = teacherDetails.phone_2 ?: "",
            qualification = qualification.type,
            designation = designation.type,
            department = department.name,
            exp = teacherDetails.exp,
            joiningDate = teacherDetails.joiningDate.toString(),
            address = address
        )
    }


    override suspend fun updateDetails(email: String, req: UpdateDetailsReq): Boolean = coroutineScope {
        val teacher = findTeacher(email) ?: return@coroutineScope false

        val emailDef = async {
            if (teacher.email.uppercase() != req.email && req.email.isNotEmpty()) query {
                teacher.email = req.email
            }
        }

        val detailsDef = async { getTeacherDetailsOnTeacherId(teacher.id.value, email) }

        val details = detailsDef.await() ?: return@coroutineScope false
        emailDef.await()

        query {
            TeacherDetailsTable.update(
                where = {
                    TeacherDetailsTable.teacherId eq teacher.id
                }
            ) {
                if (req.name.isNotEmpty()) it[this.name] = req.name
                if (details.phoneOne != req.phoneOne && req.phoneOne.isNotEmpty()) it[this.phone_1] = req.phoneOne
                if (details.phoneTwo != req.phoneTwo && req.phoneTwo.isNotEmpty()) it[this.phone_2] = req.phoneTwo
            }
        }

        if (req.qualification.isNotBlank()) {
            val qualificationDef = async { getQualificationOnType(req.qualification) }
            val qualification = qualificationDef.await() ?: return@coroutineScope false


            query {
                TeacherDetailsTable.update(
                    where = {
                        TeacherDetailsTable.teacherId eq teacher.id
                    }
                ) {
                    if (details.designationId != qualification.id.value) it[this.qualificationId] =
                        qualification.id.value
                }
            }
        }

        true
    }

    override suspend fun updateHeadDetails(email: String, req: UpdateHeadDetailsReq): Boolean = coroutineScope {
        val principalDef = async { getPrincipal() }
        val headClarkDef = async { getHeadClark() }

        val principal = principalDef.await()
        val headClark = headClarkDef.await()


        when {
            principal.email == email -> query {
                principal.name = req.name
                principal.email = req.email
            }.let { true }

            headClark.email == email -> query {
                headClark.name = req.name
                headClark.email = req.email
            }.let { true }

            else -> false
        }
    }

    override suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean = coroutineScope {
        val teacher = findTeacher(email) ?: return@coroutineScope false


        val addressType = query {
            com.poulastaa.domain.dao.address.AddressType.find {
                AddressTypeTable.type eq req.type.name
            }.singleOrNull()
        } ?: return@coroutineScope false


        val oldEntry = query {
            TeacherAddressTable.select {
                TeacherAddressTable.addressTypeId eq addressType.id and (TeacherAddressTable.teacherId eq teacher.id)
            }.singleOrNull()?.toTeacherAddress()
        } ?: return@coroutineScope false

        if (req.otherType) updateBothAddress(teacher.id.value, req, oldEntry)
        else {
            query {
                TeacherAddressTable.update(
                    where = {
                        TeacherAddressTable.addressTypeId eq addressType.id and (TeacherAddressTable.teacherId eq teacher.id)
                    }
                ) {
                    updateTeacherEntry(it, req, oldEntry)
                }
            }
        }

        true
    }

    override suspend fun storeProfilePic(
        email: String,
        fileNameWithPath: String,
    ): Boolean {
        val teacher = findTeacher(email)

        val principal = getPrincipal()

        if (teacher != null) {
            query {
                TeacherDetailsTable.update(
                    where = {
                        TeacherDetailsTable.teacherId eq teacher.id
                    }
                ) {
                    it[this.profilePic] = fileNameWithPath
                }
            }
        } else if (principal.email == email) {
            query {
                principal.profilePic = fileNameWithPath
            }
        } else {
            return false
        }

        return true
    }

    override suspend fun getProfilePic(email: String): String? = query {
        val teacher = findTeacher(email)

        if (teacher != null) {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()?.let {
                it[TeacherDetailsTable.profilePic]
            }
        } else {
            val principal = getPrincipal()

            if (principal.email == email) principal.profilePic
            else getHeadClark().profilePic
        }
    }

    override suspend fun getTeacherDetails(email: String, id: Int): TeacherDetails? = getTeacherDetailsOnTeacherId(
        id = id,
        email = email
    )

    override suspend fun getTeacherTypeOnId(id: Int): com.poulastaa.data.model.constants.TeacherType? = query {
        TeacherType.find {
            TeacherTypeTable.id eq id
        }.singleOrNull()?.let {
            when (it.type) {
                com.poulastaa.data.model.constants.TeacherType.SACT.value -> com.poulastaa.data.model.constants.TeacherType.SACT
                com.poulastaa.data.model.constants.TeacherType.PERMANENT.value -> com.poulastaa.data.model.constants.TeacherType.PERMANENT
                else -> null
            }
        }
    }

    private suspend fun updateBothAddress(
        id: Int,
        req: UpdateAddressReq,
        oldEntry: TeacherAddress,
    ) = query {
        TeacherAddressTable.update(
            where = {
                TeacherAddressTable.teacherId eq id
            }
        ) {
            updateTeacherEntry(it, req, oldEntry)
        }
    }

    private fun TeacherAddressTable.updateTeacherEntry(
        it: UpdateStatement,
        req: UpdateAddressReq,
        oldEntry: TeacherAddress,
    ) {
        it[this.houseNumb] = req.houseNo ?: oldEntry.houseNum
        it[this.street] = req.street ?: oldEntry.street
        it[this.city] = req.city ?: oldEntry.city
        it[this.zip] = req.zipCode?.toInt() ?: oldEntry.zipCode.toInt()
        it[this.state] = req.state ?: oldEntry.state
    }


    private suspend fun checkIfDetailsAlreadyFilled(id: Int) = query {
        TeacherDetailsTable.select {
            TeacherDetailsTable.teacherId eq id
        }.empty()
    }

    private suspend fun getDepartmentHead(id: Int) = query {
        !DepartmentHead.find {
            DepartmentHeadTable.teacherId eq id
        }.empty()
    }

    private suspend fun SetDetailsReq.toDetailsEntry(teacherId: EntityID<Int>): SetDetailsEntry? = coroutineScope {
        val designationDef = async {
            query {
                Designation.find {
                    DesignationTable.type.upperCase() eq this@toDetailsEntry.designation.uppercase()
                }.singleOrNull()
            }
        }

        val departmentIdDef = async {
            query {
                Department.find {
                    DepartmentTable.name.upperCase() eq this@toDetailsEntry.department.uppercase()
                }.singleOrNull()?.id
            }
        }

        val qualificationIdDef = async {
            getQualificationOnType(this@toDetailsEntry.qualification)
        }

        val designation = designationDef.await() ?: return@coroutineScope null
        val departmentId = departmentIdDef.await() ?: return@coroutineScope null
        val qualificationId = qualificationIdDef.await() ?: return@coroutineScope null


        val dbo = this@toDetailsEntry.dbo.toLocalDate() ?: return@coroutineScope null
        val joiningDate = this@toDetailsEntry.joiningDate.toLocalDate() ?: return@coroutineScope null

        val teacherTypeId = query {
            DesignationTeacherTypeRelation
                .slice(DesignationTeacherTypeRelation.teacherTypeId)
                .select {
                    DesignationTeacherTypeRelation.designationId eq designation.id.value
                }.singleOrNull().let {
                    it?.get(DesignationTeacherTypeRelation.teacherTypeId)
                }
        } ?: return@coroutineScope null

        val details = TeacherDetailsEntry(
            email = this@toDetailsEntry.email,
            teacherId = teacherId,
            teacherTypeId = teacherTypeId,
            hrmsId = this@toDetailsEntry.hrmsId,
            name = this@toDetailsEntry.name,
            phone_1 = this@toDetailsEntry.phone_1,
            phone_2 = this@toDetailsEntry.phone_2,
            bDate = dbo,
            gender = this@toDetailsEntry.sex.toString(),
            designationId = designation.id,
            departmentId = departmentId,
            joiningDate = joiningDate,
            qualificationId = qualificationId.id,
            exp = this@toDetailsEntry.exp
        )

        val address = this@toDetailsEntry.address.map {
            when (it.first) {
                AddressType.PRESENT -> {
                    AddressEntry(
                        teacherId = teacherId,
                        addressTypeId = it.first.id,
                        houseNumber = it.second.houseNumber,
                        street = it.second.street,
                        city = it.second.city,
                        zipCode = it.second.zipcode.toInt(),
                        state = it.second.state
                    )
                }

                AddressType.HOME -> {
                    AddressEntry(
                        teacherId = teacherId,
                        addressTypeId = it.first.id,
                        houseNumber = it.second.houseNumber,
                        street = it.second.street,
                        city = it.second.city,
                        zipCode = it.second.zipcode.toInt(),
                        state = it.second.state
                    )
                }
            }
        }

        SetDetailsEntry(
            details = details,
            address = address
        )
    }

    private suspend fun SetDetailsEntry.storeDetails(): Unit = coroutineScope {
        val detailsDef = async {
            this@storeDetails.details.setDetails()
        }

        val addressDef = this@storeDetails.address.map {
            async {
                it.setAddress()
            }
        }

        detailsDef.await()
        addressDef.awaitAll()
    }

    private suspend fun setLeaveBalance(
        teacherTypeId: Int,
        teacherId: Int,
        gender: Char,
    ) = coroutineScope {
        fun insertLeaveBalance(
            teacherId: Int,
            teacherTypeId: Int,
            leaveTypeId: Int,
            balance: Double,
        ) {
            LeaveBalanceTable.insertIgnore { statement ->
                statement[this.teacherId] = teacherId
                statement[this.teacherTypeId] = teacherTypeId
                statement[this.leaveTypeId] = leaveTypeId
                statement[this.leaveBalance] = balance
            }
        }

        query {
            TeacherType.find {
                TeacherTypeTable.id eq teacherTypeId
            }.single()
        }.let {
            val type = when {
                it.type.uppercase().startsWith("S") -> com.poulastaa.data.model.constants.TeacherType.SACT
                it.type.uppercase().startsWith("P") -> com.poulastaa.data.model.constants.TeacherType.PERMANENT
                else -> {
                    throw IllegalArgumentException("should now happen")
                }
            }

            when (type) {
                com.poulastaa.data.model.constants.TeacherType.SACT -> {
                    val casualLeave = 14.0
                    val medicalLeave = 20.0
                    val maternityLeave = if (gender == 'F') 135.0 else null
                    val quarantineLeave = 21.0

                    getSACTTeacherLeaveType().map { (leaveType, leaveTypeId) ->
                        async {
                            query {
                                when (leaveType) {
                                    LeaveType.ScatType.CASUAL_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = casualLeave
                                    )

                                    LeaveType.ScatType.MEDICAL_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = medicalLeave
                                    )

                                    LeaveType.ScatType.ON_DUTY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = casualLeave
                                    )

                                    LeaveType.ScatType.MATERNITY_LEAVE -> maternityLeave?.let { balance ->
                                        insertLeaveBalance(
                                            teacherId = teacherId,
                                            teacherTypeId = it.id.value,
                                            leaveTypeId = leaveTypeId,
                                            balance = balance
                                        )
                                    } ?: Unit

                                    LeaveType.ScatType.QUARANTINE_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = quarantineLeave
                                    )
                                }
                            }
                        }
                    }.awaitAll()
                }

                com.poulastaa.data.model.constants.TeacherType.PERMANENT -> {
                    val casualLeave = 14.0
                    val earnedLeave = 30.0
                    val studyLeave = 360.0
                    val specialStudyLeave = 360.0
                    val maternityLeave = if (gender == 'F') 135.0 else null
                    val quarantineLeave = 21.0
                    val medicalLeave = 20.0
                    val commutedLeave = 180.0
                    val extraOrdinaryLeave = 360.0
                    val leaveNotDueLeave = 360.0
                    val specialDisabilityLeave = 720.0

                    getPermanentTeacherLeaveType().map { (leaveType, leaveTypeId) ->
                        async {
                            query {
                                when (leaveType) {
                                    LeaveType.PermanentType.CASUAL_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = casualLeave
                                    )

                                    LeaveType.PermanentType.MEDICAL_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = medicalLeave
                                    )

                                    LeaveType.PermanentType.STUDY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = studyLeave
                                    )

                                    LeaveType.PermanentType.EARNED_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = earnedLeave
                                    )

                                    LeaveType.PermanentType.SPECIAL_STUDY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = specialStudyLeave
                                    )

                                    LeaveType.PermanentType.MATERNITY_LEAVE -> maternityLeave?.let { balance ->
                                        insertLeaveBalance(
                                            teacherId = teacherId,
                                            teacherTypeId = it.id.value,
                                            leaveTypeId = leaveTypeId,
                                            balance = balance
                                        )
                                    } ?: Unit


                                    LeaveType.PermanentType.QUARANTINE_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = quarantineLeave
                                    )

                                    LeaveType.PermanentType.COMMUTED_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = commutedLeave
                                    )

                                    LeaveType.PermanentType.EXTRAORDINARY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = extraOrdinaryLeave
                                    )

                                    LeaveType.PermanentType.COMPENSATORY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = 0.0
                                    )

                                    LeaveType.PermanentType.LEAVE_NOT_DUE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = leaveNotDueLeave
                                    )

                                    LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = specialDisabilityLeave
                                    )

                                    LeaveType.PermanentType.ON_DUTY_LEAVE -> insertLeaveBalance(
                                        teacherId = teacherId,
                                        teacherTypeId = it.id.value,
                                        leaveTypeId = leaveTypeId,
                                        balance = casualLeave
                                    )
                                }
                            }
                        }
                    }.awaitAll()
                }
            }
        }
    }

    private suspend fun TeacherDetailsEntry.setDetails() = query {
        TeacherDetailsTable.insertIgnore {
            it[this.teacherId] = this@setDetails.teacherId
            it[this.teacherTypeId] = this@setDetails.teacherTypeId
            it[this.hrmsId] = this@setDetails.hrmsId
            it[this.name] = this@setDetails.name
            it[this.phone_1] = this@setDetails.phone_1
            it[this.phone_2] = this@setDetails.phone_2
            it[this.bDate] = this@setDetails.bDate
            it[this.gender] = this@setDetails.gender
            it[this.designationId] = this@setDetails.designationId
            it[this.departmentId] = this@setDetails.departmentId
            it[this.joiningDate] = this@setDetails.joiningDate
            it[this.qualificationId] = this@setDetails.qualificationId
            it[this.exp] = this@setDetails.exp
        }
    }

    private suspend fun AddressEntry.setAddress() = query {
        TeacherAddressTable.insertIgnore {
            it[this.teacherId] = this@setAddress.teacherId
            it[this.addressTypeId] = this@setAddress.addressTypeId
            it[this.houseNumb] = this@setAddress.houseNumber
            it[this.street] = this@setAddress.street
            it[this.city] = this@setAddress.city
            it[this.zip] = this@setAddress.zipCode
            it[this.state] = this@setAddress.state
        }
    }

    private fun handleLoginEntry(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isEntry = query {
                    LogInEmail.find {
                        LogInEmailTable.email eq email
                    }.singleOrNull()
                }

                if (isEntry != null) return@launch

                val entry = query {
                    LogInEmail.new {
                        this.email = email
                    }
                }

                delay(VERIFICATION_MAIL_TOKEN_TIME)

                query {
                    entry.delete()
                }
            } catch (_: Exception) {
                return@launch
            }
        }
    }

    private suspend fun getDepartmentOnId(id: Int) = query {
        Department.find {
            DepartmentTable.id eq id
        }.single()
    }

    private suspend fun getDesignationOnId(id: Int) = query {
        Designation.find {
            DesignationTable.id eq id
        }.single()
    }

    private suspend fun getQualification(id: Int) = query {
        Qualification.find {
            QualificationTable.id eq id
        }.single()
    }

    private suspend fun getAddressOnTeacherId(id: Int) = query {
        TeacherAddressTable.select {
            TeacherAddressTable.teacherId eq id
        }.map {
            val addressType = AddressType.valueOf(
                getAddressTypeOnId(it[TeacherAddressTable.addressTypeId].value)
            )

            addressType to ReqAddress(
                houseNumber = it[TeacherAddressTable.houseNumb],
                street = it[TeacherAddressTable.street],
                city = it[TeacherAddressTable.city],
                zipcode = it[TeacherAddressTable.zip].toString(),
                state = it[TeacherAddressTable.state],
                country = it[TeacherAddressTable.country]
            )
        }
    }

    private suspend fun getAddressTypeOnId(id: Int) = query {
        com.poulastaa.domain.dao.address.AddressType.find {
            AddressTypeTable.id eq id
        }.single().type
    }

    private suspend fun getTeacherDetailsOnTeacherId(id: Int, email: String) = query {
        query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq id
            }.singleOrNull()?.toTeacherDetails(email)
        }
    }

    private suspend fun getQualificationOnType(type: String) = query {
        Qualification.find {
            QualificationTable.type.upperCase() eq type.uppercase()
        }.singleOrNull()
    }

    override suspend fun getSACTTeacherLeaveType() = query {
        LeaveType.find {
            LeaveTypeTable.type inList LeaveType.ScatType.entries.map {
                it.value
            }
        }.map {
            when (it.type) {
                LeaveType.ScatType.CASUAL_LEAVE.value -> LeaveType.ScatType.CASUAL_LEAVE to it.id.value
                LeaveType.ScatType.MEDICAL_LEAVE.value -> LeaveType.ScatType.MEDICAL_LEAVE to it.id.value
                LeaveType.ScatType.ON_DUTY_LEAVE.value -> LeaveType.ScatType.ON_DUTY_LEAVE to it.id.value
                LeaveType.ScatType.MATERNITY_LEAVE.value -> LeaveType.ScatType.MATERNITY_LEAVE to it.id.value
                else -> LeaveType.ScatType.QUARANTINE_LEAVE to it.id.value
            }
        }
    }

    override suspend fun addTeacher(email: String): Boolean = query {
        val oldTeacher = Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()

        if (oldTeacher != null) return@query false

        Teacher.new {
            this.email = email
        }

        true
    }

    override suspend fun getDepartmentHead(department: String): Teacher? = coroutineScope {
        val dep = getDepartment(department) ?: return@coroutineScope null

        val departmentHead = query {
            DepartmentHead.find {
                DepartmentHeadTable.departmentId eq dep.id
            }.firstOrNull()
        } ?: return@coroutineScope null

        query {
            Teacher.find {
                TeacherTable.id eq departmentHead.teacherId
            }.first()
        }
    }


    override suspend fun updateDepartmentHead(teacher: String, department: String): Boolean {
        val dep = getDepartment(department) ?: return false

        val teacherId = query {
            TeacherDetailsTable.select {
                TeacherDetailsTable.name eq teacher
            }.singleOrNull()?.let {
                it[TeacherDetailsTable.teacherId]
            }
        } ?: return false

        query { // updated department head
            DepartmentHead.find {
                DepartmentHeadTable.departmentId eq dep.id
            }.first().teacherId = teacherId
        }

        return true
    }

    override suspend fun getTeacherLeaveBalance(teacherId: Int): List<TeacherLeaveBalance> {
        return coroutineScope {
            query {
                LeaveBalanceTable.select {
                    LeaveBalanceTable.teacherId eq teacherId and
                            (LeaveBalanceTable.year eq LocalDate.now().year) and
                            (LeaveBalanceTable.leaveTypeId notInList listOf(6, 9, 10, 12, 13))
                }.map {
                    it[LeaveBalanceTable.leaveTypeId] to it[LeaveBalanceTable.leaveBalance]
                }.map {
                    async {
                        query {
                            LeaveType.find {
                                LeaveTypeTable.id eq it.first
                            }.first() to it.second
                        }
                    }
                }.awaitAll()
            }.map {
                TeacherLeaveBalance(
                    id = it.first.id.value,
                    name = it.first.type,
                    balance = it.second.toString()
                )
            }
        }
    }

    override suspend fun updateLeaveBalance(req: UpdateLeaveBalanceReq): Boolean {
        query {
            LeaveBalanceTable.update(
                where = {
                    LeaveBalanceTable.leaveTypeId eq req.leaveId and
                            (LeaveBalanceTable.teacherId eq req.teacherId) and
                            (LeaveBalanceTable.year eq LocalDate.now().year)
                }
            ) {
                it[this.leaveBalance] = req.value.toDouble()
            }
        }

        return true
    }

    override suspend fun deleteTeacher(id: Int): Boolean {
        coroutineScope {
            val teacher = async {
                query {
                    val teacher = Teacher.find {
                        TeacherTable.id eq id
                    }.firstOrNull() ?: return@query false

                    teacher.delete()
                }
            }

            val details = async {
                query {
                    TeacherDetailsTable.deleteWhere {
                        this.teacherId eq id
                    }
                }
            }

            val address = async {
                query {
                    TeacherAddressTable.deleteWhere {
                        this.teacherId eq id
                    }
                }
            }

            val balance = async {
                query {
                    LeaveBalanceTable.deleteWhere {
                        this.teacherId eq id
                    }
                }
            }


            val leave = async {
                query {
                    LeaveReq.find {
                        LeaveReqTable.teacherId eq id
                    }.map {
                        it.delete()
                    }
                }
            }

            teacher.await()
            details.await()
            address.await()
            balance.await()
            leave.await()
        }

        return true
    }

    private suspend fun getDepartment(department: String) = query {
        Department.find {
            DepartmentTable.name eq department
        }.firstOrNull()
    }

    private suspend fun getPermanentTeacherLeaveType() = query {
        LeaveType.find {
            LeaveTypeTable.type inList LeaveType.PermanentType.entries.map {
                it.value
            }
        }.map {
            when (it.type) {
                LeaveType.PermanentType.CASUAL_LEAVE.value -> LeaveType.PermanentType.CASUAL_LEAVE to it.id.value
                LeaveType.PermanentType.MEDICAL_LEAVE.value -> LeaveType.PermanentType.MEDICAL_LEAVE to it.id.value
                LeaveType.PermanentType.STUDY_LEAVE.value -> LeaveType.PermanentType.STUDY_LEAVE to it.id.value
                LeaveType.PermanentType.EARNED_LEAVE.value -> LeaveType.PermanentType.EARNED_LEAVE to it.id.value
                LeaveType.PermanentType.SPECIAL_STUDY_LEAVE.value -> LeaveType.PermanentType.SPECIAL_STUDY_LEAVE to it.id.value
                LeaveType.PermanentType.MATERNITY_LEAVE.value -> LeaveType.PermanentType.MATERNITY_LEAVE to it.id.value
                LeaveType.PermanentType.QUARANTINE_LEAVE.value -> LeaveType.PermanentType.QUARANTINE_LEAVE to it.id.value
                LeaveType.PermanentType.COMMUTED_LEAVE.value -> LeaveType.PermanentType.COMMUTED_LEAVE to it.id.value
                LeaveType.PermanentType.EXTRAORDINARY_LEAVE.value -> LeaveType.PermanentType.EXTRAORDINARY_LEAVE to it.id.value
                LeaveType.PermanentType.COMPENSATORY_LEAVE.value -> LeaveType.PermanentType.COMPENSATORY_LEAVE to it.id.value
                LeaveType.PermanentType.LEAVE_NOT_DUE.value -> LeaveType.PermanentType.LEAVE_NOT_DUE to it.id.value
                else -> LeaveType.PermanentType.SPECIAL_DISABILITY_LEAVE to it.id.value
            }
        }
    }
}