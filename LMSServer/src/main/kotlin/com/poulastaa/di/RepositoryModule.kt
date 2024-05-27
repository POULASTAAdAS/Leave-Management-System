package com.poulastaa.di

import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.data.repository.TeacherRepository
import com.poulastaa.data.repository.leave.LeaveUtilsRepository
import com.poulastaa.data.repository.leave.LeaveWrapper
import com.poulastaa.domain.repository.ServiceRepositoryImpl
import com.poulastaa.domain.repository.TeacherRepositoryImpl
import com.poulastaa.domain.repository.leave.LeaveUtilsRepositoryImpl
import org.koin.dsl.module

fun provideDatabase() = module {
    single<TeacherRepository> {
        TeacherRepositoryImpl()
    }

    single<LeaveUtilsRepository> {
        LeaveUtilsRepositoryImpl()
    }

    single<LeaveWrapper> {
        LeaveWrapper(
            leaveUtils = get()
        )
    }

    single<ServiceRepository> {
        ServiceRepositoryImpl(
            jwtRepo = get(),
            teacher = get(),
            leave = get()
        )
    }
}