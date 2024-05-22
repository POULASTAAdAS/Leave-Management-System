package com.poulastaa.data.model.auth.res

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    PRINCIPLE,
    HEAD_OF_THE_DEPARTMENT,
    TEACHER,
    NON
}