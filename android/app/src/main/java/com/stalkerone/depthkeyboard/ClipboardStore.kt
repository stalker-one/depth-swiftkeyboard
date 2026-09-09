package com.stalkerone.depthkeyboard

import android.content.Context

class ClipboardStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("depth_clipboard", Context.MODE_PRIVATE)
    private val key = "items"
    private val maxItems = 50

    fun items(): List<String> = prefs.getString(key, "")
        .orEmpty().split("\u001f").filter { it.isNotBlank() }

    fun add(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val next = listOf(clean) + items().filterNot { it == clean }
        prefs.edit().putString(key, next.take(maxItems).joinToString("\u001f")).apply()
    }

    fun remove(text: String) {
        prefs.edit().putString(key, items().filterNot { it == text }.joinToString("\u001f")).apply()
    }

    fun clear() { prefs.edit().remove(key).apply() }
}
