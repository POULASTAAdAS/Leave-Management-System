package online.poulastaa.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import online.poulastaa.data.model.EndPoints
import online.poulastaa.data.model.auth.SaveTeacherDetailsReq
import online.poulastaa.data.model.auth.SaveTeacherDetailsResponse
import online.poulastaa.data.repository.UserServiceRepository
import online.poulastaa.utils.Constants.SECURITY_LIST
import online.poulastaa.utils.nullCheck

fun Route.saveTeacherDetails(
    service: UserServiceRepository
) {
    authenticate(configurations = SECURITY_LIST) {
        route(EndPoints.SaveTeacherDetails.route) {
            post {
                val req = call.receiveNullable<SaveTeacherDetailsReq>() ?: return@post call.respond(
                    message = SaveTeacherDetailsResponse(),
                    status = HttpStatusCode.OK
                )



                if (req.nullCheck()) return@post call.respond(
                    message = SaveTeacherDetailsResponse(),
                    status = HttpStatusCode.OK
                )

                val response = service.saveTeacherDetails(req)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}