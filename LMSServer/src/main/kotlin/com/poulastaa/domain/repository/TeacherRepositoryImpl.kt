package com.poulastaa.domain.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.constants.AddressType
import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.convertors.AddressEntry
import com.poulastaa.data.model.convertors.SetDetailsEntry
import com.poulastaa.data.model.convertors.TeacherDetailsEntry
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.department.DepartmentTable
import com.poulastaa.data.model.table.teacher.TeacherAddressTable
import com.poulastaa.data.model.table.teacher.TeacherTable
import com.poulastaa.data.model.table.utils.DesignationTable
import com.poulastaa.data.model.table.utils.QualificationTable
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.domain.dao.department.Department
import com.poulastaa.domain.dao.teacher.Teacher
import com.poulastaa.plugins.dbQuery
import com.poulastaa.utils.Designation
import com.poulastaa.utils.Qualification
import com.poulastaa.utils.toLocalDate
import kotlinx.coroutines.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select

class TeacherRepositoryImpl : TeacherRepository {
    private suspend fun findTeacher(email: String) = dbQuery {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()
    }


    override suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, TeacherType> {
        val teacher = findTeacher(email) ?: return AuthStatus.EMAIL_NOT_REGISTERED to TeacherType.NON

        val teacherType = if (teacher.teacherTypeId.value == 1) TeacherType.PERMANENT else TeacherType.SACT

        val response = dbQuery {
            TeacherDetailsTable.select {
                TeacherDetailsTable.teacherId eq teacher.id
            }.singleOrNull()
        }

        val authStatus = if (response == null) AuthStatus.SIGNUP else AuthStatus.LOGIN

        return authStatus to teacherType
    }

    override suspend fun updateVerificationStatus(email: String): VerifiedMailStatus {
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

    override suspend fun emailVerificationCheck(email: String): Boolean = dbQuery {
        Teacher.find {
            TeacherTable.email eq email
        }.singleOrNull()?.emailVerified ?: false
    }

    override suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes = withContext(Dispatchers.IO) {
        val teacher = findTeacher(email = req.email) ?: return@withContext SetDetailsRes()


        val dataDef = async { req.toDetailsEntry(teacher.id) }
        val isEntryEmptyDef = async { checkIfDetailsAlreadyFilled(teacher.id.value) }

        val data = dataDef.await() ?: return@withContext SetDetailsRes()
        val isEmpty = isEntryEmptyDef.await()

        if (!isEmpty) return@withContext SetDetailsRes(
            status = TeacherDetailsSaveStatus.ALREADY_SAVED
        )

        CoroutineScope(Dispatchers.IO).launch {
            data.storeDetails()
        }

        SetDetailsRes(
            status = TeacherDetailsSaveStatus.SAVED
        )
    }

    private suspend fun checkIfDetailsAlreadyFilled(id: Int) = dbQuery {
        TeacherDetailsTable.select {
            TeacherDetailsTable.teacherId eq id
        }.empty()
    }

    private suspend fun SetDetailsReq.toDetailsEntry(teacherId: EntityID<Int>): SetDetailsEntry? = coroutineScope {
        val designationIdDef = async {
            dbQuery {
                Designation.find {
                    DesignationTable.id eq this@toDetailsEntry.designationId
                }.singleOrNull()?.id
            }
        }

        val departmentIdDef = async {
            dbQuery {
                Department.find {
                    DepartmentTable.id eq this@toDetailsEntry.departmentId
                }.singleOrNull()?.id
            }
        }

        val qualificationIdDef = async {
            dbQuery {
                Qualification.find {
                    QualificationTable.id eq this@toDetailsEntry.qualificationId
                }.singleOrNull()?.id
            }
        }

        val designationId = designationIdDef.await() ?: return@coroutineScope null
        val departmentId = departmentIdDef.await() ?: return@coroutineScope null
        val qualificationId = qualificationIdDef.await() ?: return@coroutineScope null

        val dbo = this@toDetailsEntry.dbo.toLocalDate() ?: return@coroutineScope null
        val joiningDate = this@toDetailsEntry.joiningDate.toLocalDate() ?: return@coroutineScope null

        val details = TeacherDetailsEntry(
            email = this@toDetailsEntry.email,
            teacherId = teacherId,
            hrmsId = this@toDetailsEntry.htmsId.toString(),
            name = this@toDetailsEntry.name,
            phone_1 = this@toDetailsEntry.phone_1.toString(),
            phone_2 = this@toDetailsEntry.phone_2.toString(),
            bDate = dbo,
            gender = this@toDetailsEntry.sex.toString(),
            designationId = designationId,
            departmentId = departmentId,
            joiningDate = joiningDate,
            qualificationID = qualificationId
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
        TeacherDetailsTable.insert {
            it[this.teacherId] = this@setDetails.teacherId
            it[this.hrmsId] = this@setDetails.hrmsId
            it[this.name] = this@setDetails.name
            it[this.phone_1] = this@setDetails.phone_1
            it[this.phone_2] = this@setDetails.phone_2
            it[this.bDate] = this@setDetails.bDate
            it[this.gender] = this@setDetails.gender
            it[this.designationId] = this@setDetails.designationId
            it[this.departmentId] = this@setDetails.departmentId
            it[this.joiningDate] = this@setDetails.joiningDate
            it[this.qualificationId] = this@setDetails.qualificationID
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
}