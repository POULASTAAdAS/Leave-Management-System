package com.poulastaa.data.repository

import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.TeacherDetails
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthStatus
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.User
import com.poulastaa.data.model.auth.res.VerifiedMailStatus
import com.poulastaa.data.model.constants.TeacherType
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.details.UpdateHeadDetailsReq
import com.poulastaa.data.model.other.TeacherLeaveBalance
import com.poulastaa.data.model.other.UpdateLeaveBalanceReq
import com.poulastaa.domain.dao.leave.LeaveType
import com.poulastaa.domain.dao.teacher.Teacher

interface TeacherRepository {
    suspend fun getTeacher(email: String): Teacher?

    suspend fun getTeacherOnId(id: Int): Teacher

    suspend fun getTeacherDetailsStatus(email: String): Pair<AuthStatus, Any>

    suspend fun getTeacherWithDetails(email: String): User

    suspend fun updateSignUpVerificationStatus(email: String): VerifiedMailStatus
    suspend fun updateLogInVerificationStatus(email: String): Pair<VerifiedMailStatus, Pair<String, String>>

    suspend fun signupEmailVerificationCheck(email: String): Boolean
    suspend fun loginEmailVerificationCheck(email: String): Boolean
    suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes

    suspend fun getTeacherDetailsRes(email: String): GetTeacherRes?

    suspend fun updateDetails(email: String, req: UpdateDetailsReq): Boolean
    suspend fun updateHeadDetails(email: String, req: UpdateHeadDetailsReq): Boolean

    suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean

    suspend fun storeProfilePic(email: String, fileNameWithPath: String): Boolean
    suspend fun getProfilePic(email: String): String?


    suspend fun getTeacherDetails(email: String, id: Int): TeacherDetails?

    suspend fun getTeacherTypeOnId(id: Int): TeacherType?

    suspend fun getSACTTeacherLeaveType(): List<Pair<LeaveType.ScatType, Int>>

    suspend fun addTeacher(
        email: String,
    ): Boolean

    suspend fun getDepartmentHead(department: String): Teacher?

    suspend fun updateDepartmentHead(
        teacher: String,
        department: String,
    ): Boolean

    suspend fun getTeacherLeaveBalance(teacherId: Int): List<TeacherLeaveBalance>

    suspend fun updateLeaveBalance(req: UpdateLeaveBalanceReq): Boolean

    suspend fun deleteTeacher(id: Int): Boolean
}