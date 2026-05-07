package com.project.codingchallenge.domain.order

import com.project.codingchallenge.data.network.orders.remote.models.dto.OrderStatus

data class Order(
    val id: String,
    val customerName: String,
    val address: String,
    val status: OrderStatus,
    val createdAt: String,
    val imageUrl: String,
    val notes: String,
    val updates: List<OrderUpdate> = emptyList()
)

data class OrderUpdate(
    val message: String,
    val timestamp: String,
    val status: OrderStatus,
    val note: String,
    val id: String
)
