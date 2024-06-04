package com.poulastaa.data.model.constants

import kotlinx.serialization.Serializable

@Serializable
enum class TeacherType(val value: String) {
    SACT("SACT"),
    PERMANENT("Permenent"),
}