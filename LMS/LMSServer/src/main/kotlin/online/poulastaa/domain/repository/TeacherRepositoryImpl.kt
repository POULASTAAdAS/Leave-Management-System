package online.poulastaa.domain.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.model.auth.ResponseType
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse
import online.poulastaa.data.model.table.address.TeacherDetailsTable
import online.poulastaa.data.model.table.teacher.TeacherAddressTable
import online.poulastaa.data.model.table.teacher.TeacherTable
import online.poulastaa.data.repository.TeacherRepository
import online.poulastaa.domain.dao.teacher.Teacher
import online.poulastaa.plugins.dbQuery
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertIgnore
import java.time.LocalDate

class TeacherRepositoryImpl : TeacherRepository {
    override suspend fun checkIfValidEmail(email: String): AuthResponse = dbQuery {
        Teacher.find {
            TeacherTable.email eq email
        }.firstOrNull()?.let {
            AuthResponse(
                teacherId = it.id.value,
                teacherTypeId = it.teacherTypeId.value,
                status = ResponseType.SUCCESS
            )
        } ?: AuthResponse(
            status = ResponseType.FAILURE
        )
    }

    override suspend fun saveTeacherDetails(req: SaveTeacherDetailsReq): SaveTeacherDetailsResponse =
        withContext(Dispatchers.IO) {
            dbQuery {
                val teacher = async {
                    Teacher.find {
                        TeacherTable.email eq req.email and (TeacherTable.id eq req.teacherId)
                    }.firstOrNull()
                }.await() ?: return@dbQuery SaveTeacherDetailsResponse()


                try {
                    val details = async {
                        TeacherDetailsTable.insertIgnore {
                            it[this.teacherId] = teacher.id
                            it[this.hrmsId] = req.basicDetails.hrmsId
                            it[this.phone_1] = req.basicDetails.phone_1
                            it[this.phone_2] = req.basicDetails.phone_2
                            it[this.bDate] = LocalDate.parse(req.basicDetails.bDate)
                            it[this.gender] = req.basicDetails.gender.toString()
                            it[this.designationId] = req.basicDetails.designationId
                            it[this.departmentId] = req.basicDetails.departmentId
                            it[this.joinDate] = LocalDate.parse(req.basicDetails.joinDate)
                            it[this.experience] = req.basicDetails.experience
                            it[this.qualificationId] = req.basicDetails.qualificationId
                        }
                    }

                    val address = async {
                        req.address.forEach { address ->
                            TeacherAddressTable.insertIgnore {
                                it[this.teacherId] = teacher.id
                                it[this.addressTypeId] = address.addressTypeId
                                it[this.houseNumb] = address.houseNumber
                                it[this.street] = address.street
                                it[this.city] = address.city
                                it[this.zip] = address.zip
                                it[this.state] = address.state
                                it[this.country] = address.country
                            }
                        }
                    }

                    details.await()
                    address.await()

                    SaveTeacherDetailsResponse(
                        status = ResponseType.SUCCESS
                    )
                } catch (e: Exception) {
                    SaveTeacherDetailsResponse()
                }
            }
        }
}