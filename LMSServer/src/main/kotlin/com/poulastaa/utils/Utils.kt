package com.poulastaa.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String.toLocalDate(): LocalDate? {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    return try {
        LocalDate.parse(this, formatter)
    } catch (_: Exception) {
        null
    }
}