package com.poulastaa.lms.domain.repository.auth

interface PatternValidator {
    fun matches(value: String): Boolean
}