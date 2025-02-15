package com.potatosheep.kite.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun DynamicAsyncImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String?,
    placeholder: Painter? = null,
    contentScale: ContentScale = ContentScale.Crop,
    loadingIndicatorPadding: Dp = 12.dp,
    loadingIndicatorSize: Dp = 40.dp,
) {
    var isLoading by rememberSaveable(model) { mutableStateOf(true) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = model,
            contentDescription = contentDescription,
            placeholder = placeholder,
            contentScale = contentScale,
            onSuccess = { isLoading = false },
        )

        AnimatedVisibility(
            visible = isLoading,
            exit = fadeOut()
        ) {
            Box (
                modifier = Modifier.fillMaxWidth(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = loadingIndicatorPadding)
                        .size(loadingIndicatorSize)
                )
            }
        }
    }
}