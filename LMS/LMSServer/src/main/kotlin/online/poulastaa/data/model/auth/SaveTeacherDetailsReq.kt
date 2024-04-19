package online.poulastaa.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SaveTeacherDetailsReq(
    val email: String,
    val teacherId: Int,
    val addressType: String,
    val houseNumber: String,
    val street: String,
    val city: String,
    val zip: Int,
    val state: String,
    val country: String
)
