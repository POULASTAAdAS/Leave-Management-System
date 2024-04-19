package online.poulastaa.data.repository

import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse

interface TeacherRepository {
    suspend fun checkIfValidEmail(email: String): AuthResponse

    suspend fun saveTeacherDetails(req: SaveTeacherDetailsReq): SaveTeacherDetailsResponse
}