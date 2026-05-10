package com.example.photocatalog.di

import android.content.Context
import com.example.photocatalog.data.local.TokenDataStore
import com.example.photocatalog.data.remote.api.AuthApi
import com.example.photocatalog.data.repository.UserRepositoryImpl
import com.example.photocatalog.domain.repository.UserRepository
import com.example.photocatalog.domain.usecases.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AppModule(private val context: Context) {
    
    // Ktor Client
    private val ktorClient: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }
        }
    }
    
    // API
    private val authApi: AuthApi by lazy {
        AuthApi(ktorClient)
    }
    
    // DataStore
    private val tokenDataStore: TokenDataStore by lazy {
        TokenDataStore(context)
    }
    
    // Repository
    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(authApi, tokenDataStore)
    }
    
    // UseCases
    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(userRepository)
    }
    
    val getUsersUseCase: GetUsersUseCase by lazy {
        GetUsersUseCase(userRepository)
    }
    
    val getUserDetailUseCase: GetUserDetailUseCase by lazy {
        GetUserDetailUseCase(userRepository)
    }
    
    val logoutUseCase: LogoutUseCase by lazy {
        LogoutUseCase(userRepository)
    }
}