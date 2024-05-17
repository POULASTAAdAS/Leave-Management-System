package com.poulastaa.data.repository

import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthStatus
import com.poulastaa.data.model.auth.res.EmailVerificationRes
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.VerifiedMailStatus
import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.convertors.SetDetailsEntry

interface TeacherRepository {
    suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, TeacherType>

    suspend fun updateVerificationStatus(email: String): VerifiedMailStatus

    suspend fun emailVerificationCheck(email: String): Boolean

    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes
}