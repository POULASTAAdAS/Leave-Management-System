package com.poulastaa.data.model.convertors

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDate

data class SetDetailsEntry(
    val details: TeacherDetailsEntry,
    val address: List<AddressEntry>,
)

data class TeacherDetailsEntry(
    val email: String,
    val teacherId: EntityID<Int>,
    val profilePic: String? = null,
    val teacherTypeId: Int,
    val hrmsId: String,
    val name: String,
    val phone_1: String,
    val phone_2: String? = null,
    val bDate: LocalDate,
    val gender: String,
    val designationId: EntityID<Int>,
    val departmentId: EntityID<Int>,
    val joiningDate: LocalDate,
    val qualificationId: EntityID<Int>,
    val exp: String,
)

data class AddressEntry(
    val teacherId: EntityID<Int>,
    val addressTypeId: Int,
    val houseNumber: String,
    val street: String,
    val city: String,
    val zipCode: Int,
    val state: String,
)
