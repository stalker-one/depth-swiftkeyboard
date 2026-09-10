package com.stalkerone.depthkeyboard.ime.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent

object VoiceInputController {
    const val ACTION_VOICE_INPUT = "com.stalkerone.depthkeyboard.VOICE_INPUT"
    fun intent(context: Context): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }
}
