package com.project.codingchallenge.presentation.orders.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.project.codingchallenge.core.Resource
import com.project.codingchallenge.core.base.BaseStateViewModel
import com.project.codingchallenge.domain.order.Order
import com.project.codingchallenge.domain.usecase.GetOrderDetailsUseCase
import com.project.codingchallenge.presentation.OrderDetailsRoute
import com.project.codingchallenge.presentation.orders.details.OrderDetailsViewModel.ErrorState
import com.project.codingchallenge.presentation.orders.details.OrderDetailsViewModel.OrderDetailsAction
import com.project.codingchallenge.presentation.orders.details.OrderDetailsViewModel.OrderDetailsActionState
import com.project.codingchallenge.presentation.orders.details.OrderDetailsViewModel.OrderDetailsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCase: GetOrderDetailsUseCase
) : BaseStateViewModel<OrderDetailsViewState, OrderDetailsActionState, ErrorState, OrderDetailsAction>() {


    private val orderId: String? = savedStateHandle.toRoute<OrderDetailsRoute>().id

    init {
        orderId?.let {
            viewModelScope.launch {
                fetchOrderDetails(it)
            }
        }
        viewModelScope.launch {
            actionFlow.collect {
                handleAction(it)
            }
        }
    }

    override val initialState: OrderDetailsViewState
        get() = OrderDetailsViewState()

    // The State
    data class OrderDetailsViewState(
        val order: Order? = null,
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false
    )

    // The Actions
    sealed interface OrderDetailsAction {
        object Refresh : OrderDetailsAction
        data object NavigateUp : OrderDetailsAction

    }


    private suspend fun fetchOrderDetails(id: String, isPullToRefresh: Boolean = false) {
        if (isPullToRefresh) {
            mutableViewState.update { it.copy(isRefreshing = true) }
        }

        mutableViewState.update { it.copy(isLoading = true) }


        val result = useCase(id = id)

        when (result) {
            is Resource.Success -> {
                mutableViewState.update { it.copy(order = result.data, isRefreshing = false) }
            }

            is Resource.Error -> {
                mutableViewState.update { it.copy(isRefreshing = false) }
                mutableErrorState.emit(ErrorState.CommonError(result.message))
            }

        }

    }

    private suspend fun handleAction(action: OrderDetailsAction) {
        when (action) {
            OrderDetailsAction.Refresh -> {
                fetchOrderDetails(orderId.orEmpty())
            }

            OrderDetailsAction.NavigateUp -> {
                mutableActionState.emit(OrderDetailsActionState.NavigateBackToOrderList)
            }
        }
    }


    sealed interface ErrorState {
        data class CommonError(val message: String) : ErrorState
    }

    sealed interface OrderDetailsActionState {
        data object NavigateBackToOrderList : OrderDetailsActionState
    }


}