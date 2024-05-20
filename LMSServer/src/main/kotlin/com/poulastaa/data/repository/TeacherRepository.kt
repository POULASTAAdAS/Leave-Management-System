package com.poulastaa.data.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthStatus
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.VerifiedMailStatus

interface TeacherRepository {
    suspend fun getTeacherDetailsStatus(email: String): AuthStatus

    suspend fun updateSignUpVerificationStatus(email: String): VerifiedMailStatus
    suspend fun updateLogInVerificationStatus(email: String): VerifiedMailStatus

    suspend fun signupEmailVerificationCheck(email: String): Boolean
    suspend fun loginEmailVerificationCheck(email: String): Boolean
    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes
}