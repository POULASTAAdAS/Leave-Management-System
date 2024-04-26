package online.poulastaa.data.model.auth

import kotlinx.serialization.Serializable
import online.poulastaa.domain.dao.department.Department
import online.poulastaa.domain.dao.util.Qualification
import java.util.Date

@Serializable
data class SaveTeacherDetailsReq(
    val email: String = "",
    val teacherId: Int = -1,

    val basicDetails: BasicDetails = BasicDetails(),
    val address: List<Address> = emptyList()
)

@Serializable
data class BasicDetails(
    val name: String = "",
    val hrmsId: String = "",
    val phone_1: String = "",
    val phone_2: String = "",
    val bDate: String = "",
    val gender: Char = ' ',
    val designationId: Int = -1,
    val departmentId: Int = -1,
    val joinDate: String = "",
    val experience: String = "",
    val qualificationId: Int = -1,
)

@Serializable
data class Address(
    val addressTypeId: Int = -1,
    val houseNumber: String = "",
    val street: String = "",
    val city: String = "",
    val zip: Int = -1,
    val state: String = "",
    val country: String = ""
)
