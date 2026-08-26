package org.everyvoice.aac.ui.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.everyvoice.aac.speech.VoiceState

/**
 * Says why the app is silent.
 *
 * Without this the buttons still light up, the sentence strip still fills,
 * and nothing ever speaks — which reads as a broken app rather than a phone
 * missing its voice data. Cheap and de-Googled devices are exactly the ones
 * this app is for, and exactly the ones that ship without a voice.
 */
@Composable
fun VoiceWarning(state: VoiceState) {
    if (state == VoiceState.STARTING || state == VoiceState.READY) return

    val context = LocalContext.current
    val message = when (state) {
        VoiceState.NO_VOICE_DATA ->
            "This phone has no voice installed, so nothing will be spoken."
        VoiceState.NO_ENGINE ->
            "This phone has no text-to-speech app, so nothing will be spoken."
        else -> return
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 16.sp,
            )
            TextButton(onClick = { openVoiceSettings(context) }) {
                Text("Install a voice", fontSize = 16.sp)
            }
        }
    }
}

/**
 * Opens the system's voice-data installer, falling back to the general
 * text-to-speech settings screen. Both can be absent on stripped-down
 * builds, so a failure to open is reported rather than crashing.
 */
private fun openVoiceSettings(context: Context) {
    val targets = listOf(
        Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA),
        Intent("com.android.settings.TTS_SETTINGS"),
    )
    for (intent in targets) {
        try {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (e: ActivityNotFoundException) {
            // Try the next one.
        }
    }
    Toast.makeText(
        context,
        "No voice installer on this device. Install a text-to-speech app to hear speech.",
        Toast.LENGTH_LONG,
    ).show()
}
