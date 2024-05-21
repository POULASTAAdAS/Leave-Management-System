package com.poulastaa.lms.di

import com.poulastaa.lms.data.repository.auth.EmailPatterValidator
import com.poulastaa.lms.data.repository.auth.UserDataValidator
import com.poulastaa.lms.domain.repository.auth.PatternValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideEmailPatternValidator(): PatternValidator = EmailPatterValidator

    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(cookieManager: CookieManager): OkHttpClient = OkHttpClient
        .Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()

    @Provides
    @ViewModelScoped
    fun provideUserDataValidator(
        patternValidator: PatternValidator
    ): UserDataValidator = UserDataValidator(
        patternValidator = patternValidator
    )
}