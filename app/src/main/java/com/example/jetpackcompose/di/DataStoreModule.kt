package com.example.jetpackcompose.di

import android.content.Context
import com.example.jetpackcompose.data.datastore.AuthPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideAuthPreference(@ApplicationContext context: Context): AuthPreference {
        return AuthPreference(context)
    }
}
