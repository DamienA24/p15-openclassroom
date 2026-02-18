package com.openclassroom.p15.di

import com.openclassroom.p15.data.repository.AuthRepositoryImpl
import com.openclassroom.p15.data.repository.EventRepositoryImpl
import com.openclassroom.p15.data.repository.UserRepositoryImpl
import com.openclassroom.p15.domain.repository.AuthRepository
import com.openclassroom.p15.domain.repository.EventRepository
import com.openclassroom.p15.domain.repository.UserRepository
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
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
