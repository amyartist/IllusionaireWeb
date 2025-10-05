package com.amyartist.illusionaireweb.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun OverlayImage(
    modifier: Modifier = Modifier,
    imageSource: Any?,
    imageDescription: String?,
    imageSize: Dp? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    if (imageSource == null) return

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val imageModifier = if (imageSize != null) {
            Modifier.size(imageSize)
        } else {
            Modifier
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageSource)
                .crossfade(true)
                .build(),
            contentDescription = imageDescription,
            modifier = imageModifier,
            contentScale = contentScale,
        )
    }
}
