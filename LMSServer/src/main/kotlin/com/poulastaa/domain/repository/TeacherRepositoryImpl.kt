package com.poulastaa.domain.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.constants.AddressType
import com.poulastaa.data.model.convertors.AddressEntry
import com.poulastaa.data.model.convertors.SetDetailsEntry
import com.poulastaa.data.model.convertors.TeacherDetailsEntry
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentHeadTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.teacher.TeacherAddressTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.designation.DesignationTable
import com.poulastaa.data.model.table.designation.DesignationTeacherTypeRelation
import com.poulastaa.data.model.table.teacher.TeacherTypeTable
import com.poulastaa.data.model.table.utils.LogInEmailTable
import com.poulastaa.data.model.table.utils.PrincipalTable
import com.poulastaa.data.model.table.utils.QualificationTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.domain.dao.department.Department
import com.poulastaa.domain.dao.department.DepartmentHead
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.domain.dao.teacher.TeacherType
import com.poulastaa.plugins.dbQuery
import com.poulastaa.domain.dao.utils.Designation
import com.poulastaa.domain.dao.utils.LogInEmail
import com.poulastaa.domain.dao.utils.Principal
import com.poulastaa.domain.dao.utils.Qualification
import com.poulastaa.utils.Constants.VERIFICATION_MAIL_TOKEN_TIME
import com.poulastaa.utils.toLocalDate
import kotlinx.coroutines.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*

class TeacherRepositoryImpl : TeacherRepository {
    private suspend fun findTeacher(email: String) = dbQuery {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()
    }

    private suspend fun findPrinciple(email: String) = dbQuery {
        Principal.find {
            PrincipalTable.email eq email
        }.firstOrNull()
    }

    override suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, Any> = coroutineScope {
        val teacherDef = async { findTeacher(email) }
        val principleDef = async { findPrinciple(email) }

        val teacher = teacherDef.await()
        val principal = principleDef.await()

        if (principal != null) {
            handleLoginEntry(email) // using loginIn entry to verify principle
            return@coroutineScope AuthStatus.PRINCIPLE_FOUND to principal
        }
        if (teacher == null) return@coroutineScope AuthStatus.EMAIL_NOT_REGISTERED to Unit

        val response = dbQuery {
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

    override suspend fun getTeacher(email: String): User = withContext(Dispatchers.IO) {
        val teacher = findTeacher(email) ?: return@withContext User()

        val (designationId,  departmentId) = dbQuery {
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
            dbQuery {
                Department.find {
                    DepartmentTable.id eq departmentId
                }.singleOrNull()?.name
            }
        }

        val designationDef = async {
            dbQuery {
                Designation.find {
                    DesignationTable.id eq designationId
                }.singleOrNull()
            }
        }

        val isDepartmentInChargeDef = async {
            dbQuery {
                DepartmentHead.find {
                    DepartmentHeadTable.teacherId eq teacher.id
                }.empty()
            }
        }


        val department = departmentDef.await() ?: return@withContext User()
        val designation = designationDef.await() ?: return@withContext User()
        val isDepartmentHead = !isDepartmentInChargeDef.await()

        dbQuery {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()?.let {
                User(
                    name = it[TeacherDetailsTable.name],
                    email = email,
                    profilePicUrl = it[TeacherDetailsTable.profilePic],
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
            if (!user.emailVerified) dbQuery {
                user.emailVerified = true
            }

            VerifiedMailStatus.VERIFIED
        } catch (_: Exception) {
            VerifiedMailStatus.SOMETHING_WENT_WRONG
        }
    }

    override suspend fun updateLogInVerificationStatus(email: String): Pair<VerifiedMailStatus, Pair<String, String>> {
        return try {
            val user = dbQuery {
                LogInEmail.find {
                    LogInEmailTable.email eq email
                }.singleOrNull()
            } ?: return VerifiedMailStatus.USER_NOT_FOUND to Pair("", "")

            if (!user.emailVerified) dbQuery {
                user.emailVerified = true
            }

            val teacher = findTeacher(email) ?: return VerifiedMailStatus.USER_NOT_FOUND to Pair("", "")

            val entry = dbQuery {
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

    override suspend fun signupEmailVerificationCheck(email: String): Boolean = dbQuery {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()?.emailVerified ?: false
    }


    override suspend fun loginEmailVerificationCheck(email: String): Boolean = dbQuery {
        LogInEmail.find {
            LogInEmailTable.email eq email
        }.singleOrNull()?.emailVerified ?: false
    }

    override suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes = withContext(Dispatchers.IO) {
        val teacher = findTeacher(email = req.email) ?: return@withContext SetDetailsRes()


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

        CoroutineScope(Dispatchers.IO).launch {
            data.storeDetails()
        }

        SetDetailsRes(
            status = TeacherDetailsSaveStatus.SAVED,
            isDepartmentHead = isDepartmentHead
        )
    }

    private suspend fun checkIfDetailsAlreadyFilled(id: Int) = dbQuery {
        TeacherDetailsTable.select {
            TeacherDetailsTable.teacherId eq id
        }.empty()
    }

    private suspend fun getDepartmentHead(id: Int) = dbQuery {
        DepartmentHead.find {
            DepartmentHeadTable.teacherId eq id
        }.empty()
    }

    private suspend fun SetDetailsReq.toDetailsEntry(teacherId: EntityID<Int>): SetDetailsEntry? = coroutineScope {
        val designationDef = async {
            dbQuery {
                Designation.find {
                    DesignationTable.type.upperCase() eq this@toDetailsEntry.designation.uppercase()
                }.singleOrNull()
            }
        }

        val departmentIdDef = async {
            dbQuery {
                Department.find {
                    DepartmentTable.name.upperCase() eq this@toDetailsEntry.department.uppercase()
                }.singleOrNull()?.id
            }
        }

        val qualificationIdDef = async {
            dbQuery {
                Qualification.find {
                    QualificationTable.type.upperCase() eq this@toDetailsEntry.qualification.uppercase()
                }.singleOrNull()?.id
            }
        }

        val designation = designationDef.await() ?: return@coroutineScope null
        val departmentId = departmentIdDef.await() ?: return@coroutineScope null
        val qualificationId = qualificationIdDef.await() ?: return@coroutineScope null

        val dbo = this@toDetailsEntry.dbo.toLocalDate() ?: return@coroutineScope null
        val joiningDate = this@toDetailsEntry.joiningDate.toLocalDate() ?: return@coroutineScope null

        val teacherTypeId = dbQuery {
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
            qualificationId = qualificationId,
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

    private suspend fun TeacherDetailsEntry.setDetails() = dbQuery {
        TeacherDetailsTable.insert { // todo store exp
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

    private suspend fun AddressEntry.setAddress() = dbQuery {
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
                val isEntry = dbQuery {
                    LogInEmail.find {
                        LogInEmailTable.email eq email
                    }.singleOrNull()
                }

                if (isEntry != null) return@launch

                val entry = dbQuery {
                    LogInEmail.new {
                        this.email = email
                    }
                }

                delay(VERIFICATION_MAIL_TOKEN_TIME)

                dbQuery {
                    entry.delete()
                }
            } catch (_: Exception) {
                return@launch
            }
        }
    }
}