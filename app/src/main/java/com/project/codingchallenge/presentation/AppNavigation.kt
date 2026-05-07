package com.project.codingchallenge.presentation

import OrderListScreenRoot
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.project.codingchallenge.presentation.orders.details.OrderDetailScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object CustomOrderListRoute

@Serializable
data class OrderDetailsRoute(
    val id: String
)

fun NavGraphBuilder.orderNavGraph(
    navController: NavHostController
) {
    // 1. Order List Screen
    composable<CustomOrderListRoute> {
        OrderListScreenRoot(
            onNavigateToOrderDetails = { orderId ->
                navController.navigate(OrderDetailsRoute(orderId.toString()))
            }
        )
    }

    // 2. Order Detail Screen
    composable<OrderDetailsRoute> { backStackEntry ->

        OrderDetailScreenRoot(
            onNavigateBackToOrderList = {
                navController.navigateUp()
            }
        )
    }
}