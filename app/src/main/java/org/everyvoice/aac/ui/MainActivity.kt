package org.everyvoice.aac.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.everyvoice.aac.speech.Speaker
import org.everyvoice.aac.speech.VoiceState
import org.everyvoice.aac.ui.screens.AppRoot
import org.everyvoice.aac.ui.theme.EveryVoiceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EveryVoiceTheme {
                val viewModel: AacViewModel = viewModel()
                val speaker = rememberSpeaker(viewModel)
                val voiceState by speaker.state.collectAsStateWithLifecycle()

                Column {
                    VoiceWarning(voiceState)
                    AppRoot(viewModel)
                }
            }
        }
    }
}

/**
 * Connects the view model's speak requests to the device voice, and makes
 * sure the voice shuts down when the app does.
 */
@Composable
private fun rememberSpeaker(viewModel: AacViewModel): Speaker {
    val context = LocalContext.current
    val speaker = remember { Speaker(context) }

    LaunchedEffect(speaker) {
        viewModel.speakRequests.collect { text -> speaker.speak(text) }
    }

    DisposableEffect(Unit) {
        onDispose { speaker.shutdown() }
    }

    return speaker
}

/**
 * Says why the app is silent.
 *
 * Without this the buttons still light up, the sentence strip still fills,
 * and nothing ever speaks — which reads as a broken app rather than a phone
 * missing its voice data.
 */
@Composable
private fun VoiceWarning(state: VoiceState) {
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
