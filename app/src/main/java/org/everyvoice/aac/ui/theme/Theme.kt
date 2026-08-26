package org.everyvoice.aac.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * High-contrast light theme. AAC users include people with low vision and
 * people who share the device in bright rooms; pale pastel themes fail both.
 */
private val EveryVoiceColors = lightColorScheme(
    primary = Color(0xFF1A56DB),
    onPrimary = Color.White,
    secondary = Color(0xFF0E7C66),
    onSecondary = Color.White,
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    background = Color(0xFFF3F4F6),
    onBackground = Color(0xFF111827),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF111827),
    error = Color(0xFFB91C1C),
)

@Composable
fun EveryVoiceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EveryVoiceColors,
        content = content,
    )
}
