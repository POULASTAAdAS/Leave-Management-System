package online.poulastaa.domain.repository

import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.model.auth.ResponseType
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse
import online.poulastaa.data.model.table.teacher.TeacherTable
import online.poulastaa.data.repository.TeacherRepository
import online.poulastaa.domain.dao.teacher.Teacher
import online.poulastaa.plugins.dbQuery

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

    override suspend fun saveTeacherDetails(req: SaveTeacherDetailsReq): SaveTeacherDetailsResponse {
        return SaveTeacherDetailsResponse()
    }
}