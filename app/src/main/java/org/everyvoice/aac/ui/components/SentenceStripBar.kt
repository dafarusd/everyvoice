package org.everyvoice.aac.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.everyvoice.aac.engine.Tile

/**
 * The sentence strip: tapped words line up left to right, and one press on
 * SPEAK says the whole sentence. Backspace removes the last word; ✕ clears.
 *
 * The strip never hides. Communication that requires navigation is
 * communication that arrives too late.
 *
 * It also never hides *behind* anything. Android 15 forces apps that target
 * SDK 35 to draw edge to edge, and without an inset the strip lands under the
 * navigation bar: measured on a Galaxy A15 the nav bar owns [0,2205][1080,2340]
 * and SPEAK sat entirely inside it, so tapping the visible button pressed the
 * system Back key instead. safeDrawing covers the navigation bar, the display
 * cutout and the keyboard, so the strip stays reachable in every state.
 */
@Composable
fun SentenceStripBar(
    tiles: List<Tile>,
    onSpeak: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (tiles.isEmpty()) {
                    Text(
                        text = "Tap words to build a sentence",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                } else {
                    tiles.forEach { tile ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                        ) {
                            Text(
                                text = tile.label,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 6.dp,
                                ),
                            )
                        }
                    }
                }
            }

            FilledTonalButton(onClick = onBackspace, enabled = tiles.isNotEmpty()) {
                Text("⌫", fontSize = 18.sp)
            }
            FilledTonalButton(onClick = onClear, enabled = tiles.isNotEmpty()) {
                Text("✕", fontSize = 18.sp)
            }
            Button(onClick = onSpeak, enabled = tiles.isNotEmpty()) {
                Text("🔊 Speak", fontSize = 17.sp)
            }
        }
    }
}
