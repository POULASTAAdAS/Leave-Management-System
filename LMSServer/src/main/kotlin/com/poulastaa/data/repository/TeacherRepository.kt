package com.poulastaa.data.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthStatus
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.User
import com.poulastaa.data.model.auth.res.VerifiedMailStatus

interface TeacherRepository {
    suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, Any>

    suspend fun getTeacher(email: String): User

    suspend fun updateSignUpVerificationStatus(email: String): VerifiedMailStatus
    suspend fun updateLogInVerificationStatus(email: String): Pair<VerifiedMailStatus, Pair<String, String>>

    suspend fun signupEmailVerificationCheck(email: String): Boolean
    suspend fun loginEmailVerificationCheck(email: String): Boolean
    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes
}