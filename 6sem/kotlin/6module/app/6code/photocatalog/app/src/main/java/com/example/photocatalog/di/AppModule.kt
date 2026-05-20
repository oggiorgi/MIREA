package com.example.photocatalog.di

import com.example.photocatalog.data.repository.NobelPrizeRepositoryImpl
import com.example.photocatalog.domain.repository.NobelPrizeRepository
import com.example.photocatalog.domain.usecase.FilterLaureatesUseCase
import com.example.photocatalog.domain.usecase.GetLaureatesUseCase

object AppModule {

    fun provideNobelPrizeRepository(): NobelPrizeRepository {
        return NobelPrizeRepositoryImpl()
    }

    fun provideGetLaureatesUseCase(): GetLaureatesUseCase {
        return GetLaureatesUseCase(provideNobelPrizeRepository())
    }

    fun provideFilterLaureatesUseCase(): FilterLaureatesUseCase {
        return FilterLaureatesUseCase()
    }
}