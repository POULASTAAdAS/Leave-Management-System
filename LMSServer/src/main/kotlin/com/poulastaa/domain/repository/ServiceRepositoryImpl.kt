package com.poulastaa.domain.repository

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.GetTeacherRes
import com.poulastaa.data.model.auth.req.SetDetailsReq
import com.poulastaa.data.model.auth.res.*
import com.poulastaa.data.model.details.UpdateAddressReq
import com.poulastaa.data.model.details.UpdateDetailsReq
import com.poulastaa.data.repository.JWTRepository
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.domain.dao.utils.Principal
import com.poulastaa.invalidTokenList
import com.poulastaa.utils.Constants.LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.Constants.SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY
import com.poulastaa.utils.sendEmail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceRepositoryImpl(
    private val jwtRepo: JWTRepository,
    private val teacher: TeacherRepository
) : ServiceRepository {
    override suspend fun auth(email: String): AuthRes {
        if (!validateEmail(email)) return AuthRes()

        val response = teacher.getTeacherDetailsStatus(email)

        return when (response.first) {
            AuthStatus.PRINCIPLE_FOUND -> {
                val token = jwtRepo.generateLogInVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifyLogInEmail.route)

                val principle = response.second as Principal

                AuthRes(
                    authStatus = response.first,
                    user = User(
                        name = principle.name,
                        email = principle.email,
                        profilePicUrl = principle.profilePic
                    )
                )
            }

            AuthStatus.SIGNUP -> {
                val token = jwtRepo.generateSignUpVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifySignUpEmail.route)


                AuthRes(
                    authStatus = response.first
                )
            }

            AuthStatus.LOGIN -> {
                val token = jwtRepo.generateLogInVerificationMailToken(email = email)
                sendEmailVerificationMail(email, token, EndPoints.VerifyLogInEmail.route)

                val user = teacher.getTeacher(email)

                AuthRes(
                    authStatus = response.first,
                    user = user
                )
            }

            else -> AuthRes()
        }
    }

    override suspend fun updateSignUpVerificationStatus(token: String): VerifiedMailStatus {
        val result = jwtRepo.verifyJWTToken(token, SIGNUP_VERIFICATION_MAIL_TOKEN_CLAIM_KEY)
            ?: return VerifiedMailStatus.TOKEN_NOT_VALID

        if (result == VerifiedMailStatus.TOKEN_USED.name) return VerifiedMailStatus.TOKEN_USED

        invalidTokenList.add(token)

        val response = teacher.updateSignUpVerificationStatus(result)

        return response
    }

    override suspend fun updateLogInVerificationStatus(token: String): Pair<VerifiedMailStatus, Pair<String, String>> {
        val result = jwtRepo.verifyJWTToken(token, LOGIN_VERIFICATION_MAIL_TOKEN_CLAIM_KEY)
            ?: return VerifiedMailStatus.TOKEN_NOT_VALID to Pair("", "")

        if (result == VerifiedMailStatus.TOKEN_USED.name) return VerifiedMailStatus.TOKEN_USED to Pair("", "")

        invalidTokenList.add(token)

        val response = teacher.updateLogInVerificationStatus(result)

        return response
    }

    override suspend fun signUpEmailVerificationCheck(email: String): EmailVerificationRes {
        val status = teacher.signupEmailVerificationCheck(email)

        return if (status) EmailVerificationRes(status = true)
        else EmailVerificationRes()
    }

    override suspend fun loginEmailVerificationCheck(email: String): Pair<EmailVerificationRes, String?> {
        val status = teacher.loginEmailVerificationCheck(email)

        return if (status) {
            val name = teacher.getTeacher(email).name

            EmailVerificationRes(status = true) to name
        } else EmailVerificationRes() to null
    }

    override suspend fun saveTeacherDetails(req: SetDetailsReq): SetDetailsRes {
        if (!req.validateDetails()) return SetDetailsRes()

        return teacher.saveTeacherDetails(req)
    }

    override suspend fun getTeacherDetails(email: String): GetTeacherRes? = teacher.getTeacherDetails(email)

    override suspend fun updateDetails(
        email: String,
        req: UpdateDetailsReq
    ): Boolean {
        if (req.email.isNotEmpty() && !validateEmail(req.email)) return false

        return teacher.updateDetails(email, req)
    }

    override suspend fun updateAddress(email: String, req: UpdateAddressReq): Boolean =
        teacher.updateAddress(email, req)

    private fun sendEmailVerificationMail(
        toEmail: String,
        token: String,
        route: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            sendEmail( // verification mail
                to = toEmail,
                subject = "Authentication Mail",
                content = (
                        (
                                "<html>"
                                        + "<body>"
                                        + "<h1>Email Authentication</h1>"
                                        + "<p>Click the following link to verify your email:</p>"
                                        + "<a href=\"${System.getenv("BASE_URL") + route}?token=" + token
                                ) + "\">Authenticate</a>"
                                + "</body>"
                                + "</html>"
                        )
            )
        }
    }


    private fun validateEmail(email: String) =
        email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"))

    private fun SetDetailsReq.validateDetails(): Boolean {
        if (this.email.isBlank() ||
            this.name.isBlank() ||
            this.dbo.isBlank() ||
            this.exp.isBlank()
        ) return false

        if (this.sex.uppercaseChar() !in listOf('M', 'F', 'O')) return false

        if (phone_1.length != 10) return false

        if (phone_2.isNotBlank()) if (phone_2.length != 10) return false

        if (department.isBlank() ||
            hrmsId.isBlank() ||
            designation.isBlank() ||
            joiningDate.isBlank()
        ) return false

        val areAddressesValid = this.address.all {
            it.second.houseNumber.isNotBlank() &&
                    it.second.street.isNotBlank() &&
                    it.second.city.isNotBlank() &&
                    it.second.zipcode.isNotBlank() &&
                    it.second.state.isNotBlank() &&
                    it.second.country.isNotBlank()
        }

        return areAddressesValid
    }
}