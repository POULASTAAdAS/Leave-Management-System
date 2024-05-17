package com.poulastaa.di

import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.domain.repository.ServiceRepositoryImpl
import com.poulastaa.domain.repository.TeacherRepositoryImpl
import org.koin.dsl.module

fun provideDatabase() = module {
    single<TeacherRepository> {
        TeacherRepositoryImpl()
    }

    single<ServiceRepository> {
        ServiceRepositoryImpl(
            jwtRepo = get(),
            teacher = get()
        )
    }
}