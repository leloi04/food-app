package com.example.jetpackcompose.di

import com.example.jetpackcompose.data.repository.AuthRepository
import com.example.jetpackcompose.data.repository.AuthRepositoryImpl
import com.example.jetpackcompose.data.repository.FoodRepository
import com.example.jetpackcompose.data.repository.FoodRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        foodRepositoryImpl: FoodRepositoryImpl
    ): FoodRepository
}
