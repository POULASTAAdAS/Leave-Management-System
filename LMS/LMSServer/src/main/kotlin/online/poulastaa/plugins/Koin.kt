package online.poulastaa.plugins

import io.ktor.server.application.*
import online.poulastaa.di.provideDatabaseModule
import online.poulastaa.di.provideService
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(
            provideDatabaseModule(),
            provideService()
        )
    }
}