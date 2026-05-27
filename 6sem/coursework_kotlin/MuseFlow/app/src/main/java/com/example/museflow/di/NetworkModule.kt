package com.example.museflow.di

import android.content.Context
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.data.network.client.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideApiService(
        tokenManager: TokenManager,
        @ApplicationContext context: Context
    ): ApiService {
        return RetrofitClient.provideApiService(tokenManager, context)
    }
}