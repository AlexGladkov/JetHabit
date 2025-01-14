package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest

@Composable
fun PlatformImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalPlatformContext.current
    
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
} 