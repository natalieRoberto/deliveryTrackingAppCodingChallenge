package com.project.codingchallenge.data.network.orders.remote.models.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    @SerialName("customerName")
    val customerName: String,

    @SerialName("address")
    val address: String
)