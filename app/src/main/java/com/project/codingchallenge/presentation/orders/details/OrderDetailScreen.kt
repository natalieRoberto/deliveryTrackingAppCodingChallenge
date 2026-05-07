package com.project.codingchallenge.presentation.orders.details

import StatusBadge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.codingchallenge.R
import com.project.codingchallenge.core.composable.CustomAsyncImage
import com.project.codingchallenge.core.compose.rememberShimmerBrush
import com.project.codingchallenge.core.compose.utils.ActionEffect
import com.project.codingchallenge.domain.order.Order
import com.project.codingchallenge.domain.order.OrderUpdate


@Composable
fun OrderDetailScreenRoot(
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    onNavigateBackToOrderList: () -> Unit
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()

    OrderDetailScreen(
        state = state,
        onAction = {
            viewModel.action(it)
        }
    )

    ActionEffect(viewModel.actionState) { action ->
        when (action) {
            OrderDetailsViewModel.OrderDetailsActionState.NavigateBackToOrderList -> onNavigateBackToOrderList()
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    state: OrderDetailsViewModel.OrderDetailsViewState,
    onAction: (OrderDetailsViewModel.OrderDetailsAction) -> Unit,

    ) {
    val order = state.order
    val pullToRefreshState = rememberPullToRefreshState()
    Scaffold(
        topBar = {
            OrderDetailTopBar(onBackClick = {
                onAction(OrderDetailsViewModel.OrderDetailsAction.NavigateUp)
            })
        }
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState,
            onRefresh = { onAction(OrderDetailsViewModel.OrderDetailsAction.Refresh) },
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullToRefreshState,
                    isRefreshing = state.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                // 1. Image Header
                item {
                    OrderImageHeader(url = order?.imageUrl.orEmpty())
                }

                // 2. Main Info Card
                item {
                    order?.let { OrderMainInfoCard(it) }
                }

                // 3. Description Section
                item {
                    order?.let { OrderDescriptionSection(it) }
                }

                // 4. Timeline Header
                item {
                    Text(
                        text = stringResource(R.string.delivery_timeline_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                // 5. Timeline Items
                if (order?.updates?.isNotEmpty() == true) {
                    itemsIndexed(order.updates) { index, update ->
                        TimelineItem(
                            update = update,
                            isLast = index == order.updates.size - 1
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

/**
 * UI COMPONENTS EXTRACTED BELOW
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                stringResource(R.string.order_details_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun OrderImageHeader(url: String) {
    val shimmerBrush = rememberShimmerBrush()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CustomAsyncImage(
            url = url,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Square as per your expectation
                .clip(RoundedCornerShape(24.dp)) // Modern large rounding
                .background(shimmerBrush),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun OrderMainInfoCard(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_number_label, order.id),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            StatusBadge(status = order.status)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = order.customerName.ifBlank { stringResource(R.string.unknown_customer) },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = order.address.ifBlank { stringResource(R.string.no_address) },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun OrderDescriptionSection(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(Color.White)
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.order_description_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.order_description_body,
                order.customerName,
                order.status.name.lowercase(),
                order.address
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun TimelineItem(update: OrderUpdate, isLast: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(IntrinsicSize.Min) // Important for the vertical line
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Circle Indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isLast) Color.LightGray else Color(0xFF388E3C),
                        shape = CircleShape
                    )
            )
            // Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = update.status.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = update.note,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Text(
                text = update.timestamp, // Format this nicely in a real app
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
        }
    }
}
