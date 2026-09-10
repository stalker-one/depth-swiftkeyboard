package com.stalkerone.depthkeyboard.ime.sync

import android.content.Context

class SyncController(context: Context) {
    private val prefs = context.getSharedPreferences("depth_keyboard_sync", Context.MODE_PRIVATE)
    var accountId: String? get() = prefs.getString("account_id", null); set(v) = prefs.edit().putString("account_id", v).apply()
    var enabled: Boolean get() = prefs.getBoolean("enabled", false); set(v) = prefs.edit().putBoolean("enabled", v).apply()
    fun disconnect() { accountId = null; enabled = false }
    fun status(): String = if (enabled && !accountId.isNullOrBlank()) "Connected" else "Local only"
}
