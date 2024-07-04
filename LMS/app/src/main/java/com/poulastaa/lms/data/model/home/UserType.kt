package com.poulastaa.lms.data.model.home

import kotlinx.serialization.Serializable

@Serializable
enum class UserType {
    PRINCIPLE,
    HEAD_CLARK,
    PERMANENT,
    SACT,
    NON,
    LOAD
}