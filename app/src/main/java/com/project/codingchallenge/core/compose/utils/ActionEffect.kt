package com.project.codingchallenge.core.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

@Composable
fun <Action> ActionEffect(
    flow: Flow<Action>,
    onAction: suspend (Action) -> Unit
) {
    // Use rememberUpdatedState to ensure the latest 'onAction' lambda 
    // is used without restarting the LaunchedEffect
    val currentOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(flow) {
        flow.collect { action ->
            currentOnAction(action)
        }
    }
}
