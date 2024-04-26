package online.poulastaa.data.repository

import online.poulastaa.data.model.auth.AuthReq
import online.poulastaa.data.model.auth.AuthResponse
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse

interface UserServiceRepository {
    suspend fun auth(req: AuthReq): AuthResponse

    suspend fun saveTeacherDetails(req: SaveTeacherDetailsReq): SaveTeacherDetailsResponse
}