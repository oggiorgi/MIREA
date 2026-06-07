package com.example.museflow.di

import android.content.Context
import com.example.museflow.data.local.MuseFlowDatabase
import com.example.museflow.data.local.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * Модуль внедрения зависимостей для локального хранилища. 
 * Отвечает за предоставление экземпляра базы данных Room и DAO объектов.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MuseFlowDatabase {
        return MuseFlowDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTrackDao(database: MuseFlowDatabase): TrackDao {
        return database.trackDao()
    }
}