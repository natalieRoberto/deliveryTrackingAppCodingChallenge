package com.project.codingchallenge.data.network.orders.remote.api


import com.project.codingchallenge.data.network.orders.remote.models.dto.OrderDto
import com.project.codingchallenge.data.network.orders.remote.models.request.CreateOrderRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApiService {

    @GET("orders")
    suspend fun getOrders(): List<OrderDto>

    @GET("orders/{id}")
    suspend fun getOrderDetails(
        @Path("id") id: String
    ): OrderDto

    @POST("orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest
    ): OrderDto


}
