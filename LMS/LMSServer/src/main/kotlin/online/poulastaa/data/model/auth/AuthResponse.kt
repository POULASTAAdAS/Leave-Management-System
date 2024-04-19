package online.poulastaa.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val teacherId: Int = -1,
    val teacherTypeId: Int = -1,
    val status: ResponseType = ResponseType.FAILURE
)

@Serializable
enum class ResponseType {
    SUCCESS,
    FAILURE
}