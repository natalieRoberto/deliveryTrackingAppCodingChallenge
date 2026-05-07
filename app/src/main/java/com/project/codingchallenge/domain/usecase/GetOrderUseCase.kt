package com.project.codingchallenge.domain.usecase

import com.project.codingchallenge.core.Resource
import com.project.codingchallenge.data.repository.OrderRepository
import com.project.codingchallenge.domain.order.Order
import kotlinx.coroutines.delay
import javax.inject.Inject


class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {

    suspend operator fun invoke(): Resource<List<Order>> {
        return try {
            val orders = repository.getOrders()
            Resource.Success(orders)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }
}