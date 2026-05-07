import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.codingchallenge.R
import com.project.codingchallenge.core.composable.CustomAsyncImage
import com.project.codingchallenge.core.compose.LocalShimmerBrush
import com.project.codingchallenge.core.compose.rememberShimmerBrush
import com.project.codingchallenge.core.compose.utils.ActionEffect
import com.project.codingchallenge.data.network.orders.remote.models.dto.OrderStatus
import com.project.codingchallenge.domain.order.Order
import com.project.codingchallenge.presentation.orders.composable.OrderShimmerItem
import com.project.codingchallenge.presentation.orders.orderlist.OrderListViewModel

@Composable
fun OrderListScreenRoot(
    viewModel: OrderListViewModel = hiltViewModel(),
    onNavigateToOrderDetails: (Long) -> Unit
) {
    // Observe the viewState from the ViewModel
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    ActionEffect(viewModel.actionState) { action ->
        when (action) {
            is OrderListViewModel.OrderActionState.NavigateToOrderDetails -> onNavigateToOrderDetails(
                action.id
            )
        }
    }
    OrderListScreen(
        state = state,
        onAction = {
            viewModel.action(it)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    state: OrderListViewModel.OrderViewState,
    onAction: (OrderListViewModel.OrderAction) -> Unit
) {
    Scaffold(
        topBar = { OrderListTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TABS are fixed at the top
            FilterChipsRow(
                selectedFilter = state.selectedFilter,
                onFilterSelected = { status ->
                    onAction(OrderListViewModel.OrderAction.OnFilterChanged(status))
                }
            )

            // LIST is refreshable
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(OrderListViewModel.OrderAction.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    // 1. Priority: Show Shimmer if we are doing the initial fetch
                    state.isLoading -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(6) { OrderShimmerItem() }
                        }
                    }

                    // 2. Show the list if we have data (Default view: All Orders)
                    state.filteredOrders.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.filteredOrders,
                                key = { it.id }
                            ) { order ->
                                OrderCardItem(
                                    order,
                                    onClick = {
                                        onAction(
                                            OrderListViewModel.OrderAction.OnCLickOrder(
                                                order.id.toLong()
                                            )
                                        )
                                    })
                            }
                        }
                    }

                    // 3. Only show Empty State if loading is finished AND there is no data
                    !state.isRefreshing -> {
                        EmptyOrderState(onRetry = { onAction(OrderListViewModel.OrderAction.Refresh) })
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListTopBar() { // Removed 'private' so it's accessible if needed elsewhere
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.shadow(4.dp) // Optional: adds a nice shadow for "High Quality" feel
    )
}

@Composable
fun FilterChipsRow(
    selectedFilter: OrderStatus?,
    onFilterSelected: (OrderStatus?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("All") }
            )
        }
        // Iterate through all Enum values from your model
        items(OrderStatus.entries.toTypedArray()) { status ->
            FilterChip(
                selected = selectedFilter == status,
                onClick = { onFilterSelected(status) },
                label = { Text(status.name.replace("_", " ")) }
            )
        }
    }
}

@Composable
private fun OrderCardItem(
    order: Order,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {

    val shimmerBrush = if (isLoading) rememberShimmerBrush() else null
    CompositionLocalProvider(LocalShimmerBrush provides shimmerBrush) {
        Card(
            modifier = modifier.clickable {
                onClick(order.id)
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CustomAsyncImage(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    url = order.imageUrl
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 2. THE TEXT (Name and Address)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Optional: Status Badge
                    StatusBadge(order.status)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus) {
    val color = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFB300)
        OrderStatus.IN_TRANSIT -> Color(0xFFFFB3003)
        OrderStatus.DELIVERED -> Color(0xFF4CAF50)
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun EmptyOrderState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // High Quality: Use a descriptive icon or a Box with an image
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_orders_found),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_orders_description),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Let the user refresh manually from the empty state
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = stringResource(R.string.refresh_now))
        }
    }
}


