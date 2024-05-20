package com.poulastaa.lms.domain.utils

sealed interface DataError : Error {
    enum class Network : DataError {
        UNAUTHORISED,
        EMAIL_NOT_VERIFIED,
        PASSWORD_DOES_NOT_MATCH,
        NOT_FOUND,
        CONFLICT,
        NO_INTERNET,
        SERVER_ERROR,
        SERIALISATION,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL
    }
}