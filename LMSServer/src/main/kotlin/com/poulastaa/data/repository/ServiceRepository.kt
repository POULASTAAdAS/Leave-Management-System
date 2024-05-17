package com.poulastaa.data.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthRes
import com.poulastaa.data.model.auth.res.EmailVerificationRes
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.VerifiedMailStatus


interface ServiceRepository {
    suspend fun auth(email: String): AuthRes

    suspend fun updateVerificationStatus(token: String): VerifiedMailStatus

    suspend fun emailVerificationCheck(email: String): EmailVerificationRes

    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes
}