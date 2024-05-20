package com.poulastaa.lms.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(cookieManager: CookieManager): OkHttpClient = OkHttpClient
        .Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()
}