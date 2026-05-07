package com.project.codingchallenge.data.network.orders.remote

import com.project.codingchallenge.data.network.orders.remote.api.OrderApiService
import com.project.codingchallenge.data.network.orders.remote.models.dto.OrderDto
import com.project.codingchallenge.data.network.orders.remote.models.request.CreateOrderRequest
import javax.inject.Inject

interface OrdersRemoteSource {
    suspend fun getOrders(): List<OrderDto>
    suspend fun getOrderDetails(id: String): OrderDto

    suspend fun createOrder(request: CreateOrderRequest): OrderDto

}

class OrderRemoteSourceImpl @Inject constructor(
    private val apiService: OrderApiService
) : OrdersRemoteSource {
    override suspend fun getOrders(): List<OrderDto> {
        return apiService.getOrders()
    }

    override suspend fun getOrderDetails(id: String): OrderDto {
        return apiService.getOrderDetails(id)
    }

    override suspend fun createOrder(request: CreateOrderRequest): OrderDto {
        return apiService.createOrder(request)
    }
}