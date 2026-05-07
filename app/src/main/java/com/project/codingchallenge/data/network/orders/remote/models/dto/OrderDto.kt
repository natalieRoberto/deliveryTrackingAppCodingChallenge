package com.project.codingchallenge.data.network.orders.remote.models.dto


import com.project.codingchallenge.domain.order.Order
import com.project.codingchallenge.domain.order.OrderUpdate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    @SerialName("id")
    val id: String,

    @SerialName("customerName")
    val customerName: String,
    @SerialName("avatar")
    val imageUrl: String,

    @SerialName("address")
    val address: String,
    @SerialName("notes")
    val notes: String,

    @SerialName("status")
    val status: String?,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updates")
    val updates: List<OrderUpdateDto>

)

@Serializable
enum class OrderStatus {
    @SerialName("PENDING")
    PENDING,

    @SerialName("IN_TRANSIT")
    IN_TRANSIT,

    @SerialName("DELIVERED")
    DELIVERED
}

@Serializable
data class OrderUpdateDto(
    @SerialName("id")
    val id: String,

    @SerialName("status")
    val status: OrderStatus,

    @SerialName("timestamp")
    val timestamp: String,

    @SerialName("note")
    val note: String
)


fun OrderUpdateDto.toDomain(): OrderUpdate {
    return OrderUpdate(
        id = id.orEmpty(),
        status = when (status.name.uppercase()) {
            "PENDING" -> OrderStatus.PENDING
            "IN_TRANSIT" -> OrderStatus.IN_TRANSIT
            "DELIVERED" -> OrderStatus.DELIVERED
            else -> OrderStatus.PENDING // Default fallback
        },
        timestamp = timestamp ?: "Just now",
        note = note.orEmpty(),
        message = note
    )
}

fun OrderDto.toDomain(): Order {
    return Order(
        id = id,
        customerName = customerName,
        address = address,
        status = OrderStatus.entries.random(),
        createdAt = createdAt,
        updates = updates.map { dto -> dto.toDomain() },
        imageUrl = imageUrl,
        notes = notes
    )
}