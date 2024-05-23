package com.poulastaa.lms.data.model.home

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    PRINCIPLE,
    PERMANENT,
    SACT,
    NON,
    LOAD
}