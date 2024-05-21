package com.poulastaa.lms.data.repository.auth

import android.util.Patterns
import com.poulastaa.lms.domain.repository.auth.PatternValidator

object EmailPatterValidator : PatternValidator {
    override fun matches(value: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(value).matches()
}