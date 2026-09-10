package com.stalkerone.depthkeyboard.ime

import android.content.Context
import com.stalkerone.depthkeyboard.ime.core.PrivacyController
import com.stalkerone.depthkeyboard.ime.extensions.ExtensionRegistry
import com.stalkerone.depthkeyboard.ime.media.ExternalMediaRegistry
import com.stalkerone.depthkeyboard.ime.sync.SyncController
import com.stalkerone.depthkeyboard.ime.voice.VoiceInputController
import com.stalkerone.depthkeyboard.ime.nlp.SmartTypingEngine

class DepthFeatureHub(context: Context) {
    val privacy = PrivacyController(context)
    val sync = SyncController(context)
    val extensions = ExtensionRegistry()
    val media = ExternalMediaRegistry()
    val typing = SmartTypingEngine()
    fun voiceIntent() = VoiceInputController.intent(context)
}
