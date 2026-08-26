package org.everyvoice.aac.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/** What the device voice is doing, so the app can say so out loud. */
enum class VoiceState {
    /** Still binding to the engine. Nothing to report yet. */
    STARTING,

    /** A voice is installed and speech works. */
    READY,

    /** An engine exists but has no voice data for any language we tried. */
    NO_VOICE_DATA,

    /** No text-to-speech engine on this device at all. */
    NO_ENGINE,
}

/**
 * Thin wrapper over Android's on-device text-to-speech.
 *
 * Speech rate is set slightly below default (0.95). AAC users are often
 * still building language, and a voice that runs ahead of the listener
 * teaches nothing.
 *
 * Failure is reported, never swallowed. Cheap and de-Googled devices ship
 * without an engine or without voice data for the system locale, and this
 * app is aimed squarely at cheap devices. Silence with no explanation looks
 * exactly like a broken app, and the caregiver uninstalls it.
 */
class Speaker(context: Context) {

    private val _state = MutableStateFlow(VoiceState.STARTING)
    val state: StateFlow<VoiceState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e(TAG, "TTS init failed with status $status")
                _state.value = VoiceState.NO_ENGINE
                return@TextToSpeech
            }

            val engine = tts
            if (engine == null) {
                _state.value = VoiceState.NO_ENGINE
                return@TextToSpeech
            }

            // The system locale first, then English, so a phone set to a
            // language with no voice data still speaks rather than going
            // quiet. A voice in the wrong accent beats no voice at all.
            val locales = linkedSetOf(Locale.getDefault(), Locale.US, Locale.ENGLISH)
            val spoken = locales.firstOrNull { locale ->
                val result = engine.setLanguage(locale)
                result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            }

            if (spoken == null) {
                Log.w(TAG, "No TTS voice data for any of $locales")
                _state.value = VoiceState.NO_VOICE_DATA
                return@TextToSpeech
            }

            engine.setSpeechRate(SPEECH_RATE)
            _state.value = VoiceState.READY
        }
    }

    /** Speaks [text] aloud, replacing anything currently being said. */
    fun speak(text: String) {
        val engine = tts ?: return
        if (_state.value != VoiceState.READY || text.isBlank()) return

        // A short silence opens the audio path before the first phoneme.
        // Without it the opening sound is clipped: on a Galaxy A15 the
        // sentence "I want." was heard as "o want". The first word in AAC is
        // disproportionately "I", "no", "stop" or "help", so a clipped
        // opening is a lost word rather than a rough edge.
        engine.playSilentUtterance(LEAD_IN_MS, TextToSpeech.QUEUE_FLUSH, "$UTTERANCE_ID-lead")
        engine.speak(text, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        _state.value = VoiceState.STARTING
    }

    private companion object {
        const val TAG = "Speaker"
        const val SPEECH_RATE = 0.95f

        /** Silence before each utterance, in milliseconds. */
        const val LEAD_IN_MS = 120L
        const val UTTERANCE_ID = "everyvoice"
    }
}
