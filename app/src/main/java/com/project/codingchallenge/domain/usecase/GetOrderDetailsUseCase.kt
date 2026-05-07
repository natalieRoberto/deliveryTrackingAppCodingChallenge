package com.project.codingchallenge.domain.usecase

import com.project.codingchallenge.core.Resource
import com.project.codingchallenge.data.repository.OrderRepository
import com.project.codingchallenge.domain.order.Order
import javax.inject.Inject

class GetOrderDetailsUseCase @Inject constructor(
    private val repository: OrderRepository
) {

    suspend operator fun invoke(
        id: String
    ): Resource<Order> {

        return try {

            val order = repository.getOrderDetails(id)

            Resource.Success(order)

        } catch (e: Exception) {

            Resource.Error(
                message = e.message ?: "Unknown error occurred",
                throwable = e
            )
        }
    }
}