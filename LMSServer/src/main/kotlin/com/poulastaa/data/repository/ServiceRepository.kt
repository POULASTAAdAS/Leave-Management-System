package com.poulastaa.data.repository

import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthRes
import com.poulastaa.data.model.auth.res.EmailVerificationRes
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.VerifiedMailStatus
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.leave.ApplyLeaveReq
import com.poulastaa.data.model.leave.ApplyLeaveRes
import com.poulastaa.data.model.leave.GetBalanceRes
import java.io.File


interface ServiceRepository {
    suspend fun auth(email: String): AuthRes

    suspend fun updateSignUpVerificationStatus(token: String): VerifiedMailStatus
    suspend fun updateLogInVerificationStatus(token: String): Pair<VerifiedMailStatus, Pair<String, String>>

    suspend fun signUpEmailVerificationCheck(email: String): EmailVerificationRes
    suspend fun loginEmailVerificationCheck(email: String): Pair<EmailVerificationRes, String?>

    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes

    suspend fun getTeacherDetails(email: String): GetTeacherRes?

    suspend fun updateDetails(email: String, req: UpdateDetailsReq): Boolean

    suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean

    suspend fun storeProfilePic(email: String, name: String): Boolean
    suspend fun getProfilePic(email: String): File?

    suspend fun getLeaveBalance(type: String, email: String): GetBalanceRes

    suspend fun handleLeaveReq(
        req: ApplyLeaveReq,
        filePath: String?
    ): ApplyLeaveRes
}