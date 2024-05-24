package com.poulastaa.utils

import com.poulastaa.data.model.TeacherDetails
import com.poulastaa.data.model.convertors.TeacherProfileEntry
import com.poulastaa.data.model.details.TeacherAddress
import com.poulastaa.data.model.table.address.TeacherDetailsTable
import com.poulastaa.data.model.table.teacher.TeacherAddressTable
import com.poulastaa.data.model.table.teacher.TeacherProfilePicTable
import org.jetbrains.exposed.sql.ResultRow
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

fun ResultRow.toTeacherDetails(email: String) = TeacherDetails(
    name = this[TeacherDetailsTable.name],
    email = email,
    teacherTypeId = this[TeacherDetailsTable.teacherTypeId].value,
    profileImage = this[TeacherDetailsTable.profilePic],
    phoneOne = this[TeacherDetailsTable.phone_1],
    phoneTwo = this[TeacherDetailsTable.phone_2],
    designationId = this[TeacherDetailsTable.designationId].value,
    departmentId = this[TeacherDetailsTable.departmentId].value,
    joiningDate = this[TeacherDetailsTable.joiningDate].toString(),
    dob = this[TeacherDetailsTable.bDate].toString(),
    exp = this[TeacherDetailsTable.exp],
    gender = this[TeacherDetailsTable.gender]
)

fun ResultRow.toTeacherAddress() = TeacherAddress(
    houseNum = this[TeacherAddressTable.houseNumb],
    street = this[TeacherAddressTable.street],
    city = this[TeacherAddressTable.city],
    zipCode = this[TeacherAddressTable.zip].toString(),
    state = this[TeacherAddressTable.state]
)

fun ResultRow.toTeacherProfilePic() = TeacherProfileEntry(
    id = this[TeacherProfilePicTable.teacherId].value,
    name = this[TeacherProfilePicTable.name],
    profilePic = this[TeacherProfilePicTable.profilePic]
)