package com.project.codingchallenge.core.compose

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Defaults for the animation values
object ShimmerDefaults {
    const val TARGET_VALUE = 1000f
    const val DURATION_MS = 1200
}

@Composable
fun rememberShimmerBrush(
    colors: List<Color> = listOf(
        Color(0xFFEBEBF4), // Light Gray
        Color(0xFFF4F4F4), // Lighter Gray (the "highlight")
        Color(0xFFEBEBF4)  // Light Gray
    )
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = ShimmerDefaults.TARGET_VALUE,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ShimmerDefaults.DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    return Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}
