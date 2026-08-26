package org.everyvoice.aac.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.everyvoice.aac.speech.Speaker
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

                AppRoot(viewModel, voiceState)
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
