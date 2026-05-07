package com.project.codingchallenge.core.composable

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun CustomAsyncImage(
    modifier: Modifier = Modifier,
    url: String,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .listener(onError = { _, error ->
                Log.e("CoilError", "Reason ${error.throwable.message}")
            })
            .crossfade(true)
            .build(),
        contentDescription = "Book Image",
        contentScale = contentScale

    )
}