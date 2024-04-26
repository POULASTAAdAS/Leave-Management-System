package online.poulastaa.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import online.poulastaa.data.repository.UserServiceRepository
import online.poulastaa.routes.auth
import online.poulastaa.routes.saveTeacherDetails
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val service: UserServiceRepository by inject()

    routing {
        auth(service)
        saveTeacherDetails(service)
    }
}
