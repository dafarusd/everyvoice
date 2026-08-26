package org.everyvoice.aac.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * One tappable tile: photo or emoji on top, big label underneath.
 *
 * Touch targets are intentionally oversized. Many AAC users have motor
 * impairments; a tile that is hard to hit is a word that cannot be said.
 */
@Composable
fun TileButton(
    label: String,
    icon: String,
    imagePath: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (imagePath != null) {
                TileImage(
                    imagePath = imagePath,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            } else if (icon.isNotEmpty()) {
                Text(text = icon, fontSize = 36.sp)
            }
            Text(
                text = label,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun TileImage(imagePath: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, imagePath) {
        value = withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
        }
    }
    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            // Fit, never Crop. The label leaves the image slot wider than it
            // is tall, so cropping a portrait photo shows a horizontal band
            // through the middle of it — a picture of a person becomes their
            // chin. Someone who cannot read is navigating by this picture, so
            // showing all of it small beats showing part of it large.
            contentScale = ContentScale.Fit,
            modifier = modifier,
        )
    }
}
