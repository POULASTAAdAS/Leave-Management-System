package com.poulastaa.data.repository

import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.req.AddTeacherReq
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.AuthRes
import com.poulastaa.data.model.auth.res.EmailVerificationRes
import com.poulastaa.data.model.auth.res.SetDetailsRes
import com.poulastaa.data.model.auth.res.VerifiedMailStatus
import com.poulastaa.data.model.department.GetDepartmentInChargeRes
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.model.details.UpdateHeadDetailsReq
import com.poulastaa.data.model.leave.*
import com.poulastaa.data.model.other.GetDepartmentTeacher
import com.poulastaa.data.model.other.ResponseTeacher
import com.poulastaa.data.model.other.TeacherLeaveBalance
import com.poulastaa.data.model.other.UpdateLeaveBalanceReq
import com.poulastaa.data.report.ReportResponse
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
    suspend fun updateHeadDetails(email: String, req: UpdateHeadDetailsReq): Boolean

    suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean

    suspend fun storeProfilePic(email: String, name: String): Boolean
    suspend fun getProfilePic(email: String): File?

    suspend fun getLeaveBalance(type: String, email: String): GetBalanceRes

    suspend fun handleLeaveReq(
        req: ApplyLeaveReq,
        filePath: String?,
    ): ApplyLeaveRes

    suspend fun getLeaveHistory(
        email: String,
        page: Int,
        pageSize: Int,
    ): List<LeaveHistoryRes>

    suspend fun getApproveLeave(
        email: String,
        page: Int,
        pageSize: Int,
    ): List<LeaveApproveRes>

    suspend fun handleLeave(
        req: HandleLeaveReq,
        email: String,
    ): Boolean

    suspend fun viewLeave(
        department: String,
        teacher: String,
        email: String,
        page: Int,
        pageSize: Int,
    ): List<ViewLeaveSingleRes>

    suspend fun isStillDepartmentInCharge(
        email: String,
    ): Boolean

    suspend fun getDepartmentInCharge(
        email: String,
        departmentName: String,
    ): GetDepartmentInChargeRes

    suspend fun addTeacher(
        req: AddTeacherReq,
    ): Boolean

    suspend fun updateDepartmentHead(
        teacher: String,
        department: String,
    ): Boolean

    suspend fun getDepartmentTeacher(department: String): GetDepartmentTeacher

    suspend fun getTeacherLeaveBalance(teacherId: Int): List<TeacherLeaveBalance>

    suspend fun updateLeaveBalance(req: UpdateLeaveBalanceReq): Boolean

    suspend fun getReport(
        department: String,
        type: String,
        teacher: String,
    ): List<ReportResponse>

    suspend fun getTeacherToDelete(department: String): List<ResponseTeacher>
}