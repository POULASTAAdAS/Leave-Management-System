package online.poulastaa.di

import online.poulastaa.data.repository.TeacherRepository
import online.poulastaa.data.repository.UserServiceRepository
import online.poulastaa.domain.repository.TeacherRepositoryImpl
import online.poulastaa.domain.repository.UserServiceRepositoryImpl
import org.koin.dsl.module

fun provideDatabaseModule() = module {
    single<TeacherRepository> {
        TeacherRepositoryImpl()
    }
}

fun provideService() = module {
    single<UserServiceRepository> {
        UserServiceRepositoryImpl(
            teacher = get()
        )
    }
}