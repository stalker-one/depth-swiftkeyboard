package com.stalkerone.depthkeyboard.ime.core

import android.content.Context

class PrivacyController(context: Context) {
    private val prefs = context.getSharedPreferences("depth_keyboard_privacy", Context.MODE_PRIVATE)
    var incognito: Boolean get() = prefs.getBoolean("incognito", false); set(v) = prefs.edit().putBoolean("incognito", v).apply()
    var learningEnabled: Boolean get() = prefs.getBoolean("learning", true); set(v) = prefs.edit().putBoolean("learning", v).apply()
    var clipboardCaptureEnabled: Boolean get() = prefs.getBoolean("clipboard_capture", true); set(v) = prefs.edit().putBoolean("clipboard_capture", v).apply()
    var cloudSyncEnabled: Boolean get() = prefs.getBoolean("cloud_sync", false); set(v) = prefs.edit().putBoolean("cloud_sync", v).apply()
    fun canLearn() = learningEnabled && !incognito
    fun canCaptureClipboard() = clipboardCaptureEnabled && !incognito
}
