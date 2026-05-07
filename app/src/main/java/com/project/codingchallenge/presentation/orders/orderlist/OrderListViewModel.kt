package com.project.codingchallenge.presentation.orders.orderlist

import androidx.lifecycle.viewModelScope
import com.project.codingchallenge.core.Resource
import com.project.codingchallenge.core.base.BaseStateViewModel
import com.project.codingchallenge.data.network.orders.remote.models.dto.OrderStatus
import com.project.codingchallenge.domain.order.Order
import com.project.codingchallenge.domain.usecase.GetOrdersUseCase
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel.ErrorState
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel.OrderAction
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel.OrderActionState
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel.OrderActionState.NavigateToOrderDetails
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel.OrderViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val useCase: GetOrdersUseCase,
) :
    BaseStateViewModel<OrderViewState, OrderActionState, ErrorState, OrderAction>() {
    override val initialState: OrderViewState
        get() = OrderViewState()

    init {
        viewModelScope.launch {
            actionFlow.collect {
                handleAction(it)
            }
        }
        fetchOrders()
    }


    private fun fetchOrders(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isRefreshing = isPullToRefresh,
                    isLoading = !isPullToRefresh
                )
            }

            val result = useCase()

            updateState { state ->
                val allOrders = if (result is Resource.Success) result.data else emptyList()

                // If selectedFilter is null (which it is at launch), 'filtered' becomes 'allOrders'
                val filtered = if (state.selectedFilter == null) {
                    allOrders
                } else {
                    allOrders.filter { it.status == state.selectedFilter }
                }

                state.copy(
                    orders = allOrders,
                    filteredOrders = filtered,
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    private fun onFilterChanged(status: OrderStatus?) {
        mutableViewState.update { currentState ->
            currentState.copy(
                selectedFilter = status,
                filteredOrders = applyFilter(currentState.orders, status)
            )
        }
    }

    private suspend fun handleAction(action: OrderAction) {
        when (action) {
            is OrderAction.OnCLickOrder -> {
                mutableActionState.emit(
                    NavigateToOrderDetails(
                        id = action.id
                    )
                )
            }

            is OrderAction.OnFilterChanged -> onFilterChanged(action.status)
            OrderAction.Refresh -> {
                fetchOrders(true)
            }
        }
    }


    private fun applyFilter(orders: List<Order>, status: OrderStatus?): List<Order> {
        return if (status == null) orders else orders.filter { it.status == status }
    }

    data class OrderViewState(
        val orders: List<Order> = emptyList(),// Original data
        val filteredOrders: List<Order> = emptyList(),  // Data shown after filters
        val selectedFilter: OrderStatus? = null,        // Null = "All"
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
    )


    sealed class OrderAction {
        data class OnCLickOrder(val id: Long) : OrderAction()
        data class OnFilterChanged(val status: OrderStatus?) : OrderAction()
        data object Refresh : OrderAction()
    }

    sealed interface OrderActionState {
        data class NavigateToOrderDetails(val id: Long) : OrderActionState
    }

    sealed interface ErrorState {
        data class CommonError(val message: String) : ErrorState
    }
}