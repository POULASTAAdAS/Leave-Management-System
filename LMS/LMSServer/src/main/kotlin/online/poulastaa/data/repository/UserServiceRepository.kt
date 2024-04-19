package online.poulastaa.data.repository

import online.poulastaa.data.model.auth.AuthReq
import online.poulastaa.data.model.auth.AuthResponse

interface UserServiceRepository {
    suspend fun auth(req: AuthReq): AuthResponse
}