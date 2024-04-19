package online.poulastaa

import io.ktor.server.application.*
import online.poulastaa.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureSecurity()
    configureRouting()
    configureDatabase()
    configureSession()
}
