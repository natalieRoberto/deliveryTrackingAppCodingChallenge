package com.project.codingchallenge.data.network.di

import com.project.codingchallenge.data.network.orders.remote.OrderRemoteSourceImpl
import com.project.codingchallenge.data.network.orders.remote.OrdersRemoteSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteSourceModule {

    @Binds
    @Singleton
    abstract fun bindOrderRemoteSource(
        orderRemoteSourceImpl: OrderRemoteSourceImpl
    ): OrdersRemoteSource
}