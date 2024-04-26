package online.poulastaa.utils

import online.poulastaa.data.model.auth.SaveTeacherDetailsReq

fun SaveTeacherDetailsReq.nullCheck() = listOf(
    email, teacherId
).any { it.toString().isEmpty() || it == -1 }
