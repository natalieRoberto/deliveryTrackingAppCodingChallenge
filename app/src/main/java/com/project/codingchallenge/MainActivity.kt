package com.project.codingchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.project.codingchallenge.presentation.CustomOrderListRoute
import com.project.codingchallenge.presentation.orderNavGraph
import com.project.codingchallenge.presentation.ui.theme.CodingChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Required for Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodingChallengeTheme { // Use your AppTheme wrapper
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Initialize the NavController
                    val navController = rememberNavController()

                    // Define the Navigation Graph
                    NavHost(
                        navController = navController,
                        startDestination = CustomOrderListRoute
                    ) {
                        // 1. Order List Screen
                        orderNavGraph(
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

