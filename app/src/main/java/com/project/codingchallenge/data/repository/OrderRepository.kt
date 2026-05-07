package com.project.codingchallenge.data.repository

import com.project.codingchallenge.data.network.orders.remote.OrdersRemoteSource
import com.project.codingchallenge.data.network.orders.remote.models.dto.toDomain
import com.project.codingchallenge.data.network.orders.remote.models.request.CreateOrderRequest
import com.project.codingchallenge.domain.order.Order
import javax.inject.Inject

interface OrderRepository {

    suspend fun getOrders(): List<Order>
    suspend fun getOrderDetails(id: String): Order

    suspend fun createOrder(request: CreateOrderRequest): Order
}

class OrderRepositoryImpl @Inject constructor(
    private val remoteSource: OrdersRemoteSource
) : OrderRepository {
    override suspend fun getOrders(): List<Order> {
        return remoteSource.getOrders().map { dto -> dto.toDomain() }
    }

    override suspend fun getOrderDetails(id: String): Order {
        return remoteSource.getOrderDetails(id).toDomain()
    }

    override suspend fun createOrder(request: CreateOrderRequest): Order {
        return remoteSource.createOrder(request).toDomain()
    }

}