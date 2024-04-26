package online.poulastaa.domain.repository

import online.poulastaa.data.model.auth.AuthReq
import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse
import online.poulastaa.data.repository.TeacherRepository
import online.poulastaa.data.repository.UserServiceRepository

class UserServiceRepositoryImpl(
    private val teacher: TeacherRepository
) : UserServiceRepository {
    override suspend fun auth(req: AuthReq): AuthResponse = teacher.checkIfValidEmail(email = req.email)

    override suspend fun saveTeacherDetails(req: SaveTeacherDetailsReq): SaveTeacherDetailsResponse =
        teacher.saveTeacherDetails(req)
}