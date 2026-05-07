package com.project.codingchallenge.data.network.di

import com.project.codingchallenge.data.repository.OrderRepository
import com.project.codingchallenge.data.repository.OrderRepositoryImpl
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
    abstract fun bindOrderRemoteSource(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}