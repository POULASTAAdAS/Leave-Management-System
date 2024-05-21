package com.poulastaa.lms.data.repository.auth

import com.poulastaa.lms.domain.repository.auth.PatternValidator
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UserDataValidator @Inject constructor(
    private val patternValidator: PatternValidator
) {
    enum class UserNameState {
        CAN_NOT_CONTAIN_UNDERSCORE,
        CANT_BE_EMPTY,
        VALID
    }


    fun isValidEmail(email: String): Boolean = patternValidator.matches(email.trim())

    fun isValidHrmsId(hrmsId: String): Boolean = !hrmsId.contains(' ')

    fun isValidUserName(name: String): UserNameState {
        if (name.trim().isEmpty()) return UserNameState.CANT_BE_EMPTY

        if (name.trim().contains("_")) return UserNameState.CAN_NOT_CONTAIN_UNDERSCORE
        return UserNameState.VALID
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber.trim().length > PHONE_NUMBER_LENGTH) return false

        return try {
            phoneNumber.toLong()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun isValidZip(number: String): Boolean = try {
        number.toLong()
        true
    } catch (e: Exception) {
        false
    }

    private companion object {
        const val PHONE_NUMBER_LENGTH = 10
    }
}