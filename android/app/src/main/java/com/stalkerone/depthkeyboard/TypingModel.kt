package com.stalkerone.depthkeyboard

import android.content.Context
import java.util.Locale

class TypingModel(private val context: Context) {
    private val prefs = context.getSharedPreferences("depth_typing_model", Context.MODE_PRIVATE)
    private val maxWords = 500
    private val counts = "counts"
    private val pairs = "pairs"

    private fun decode(key: String): MutableMap<String, Int> = prefs.getString(key, "").orEmpty()
        .split("\u001f").filter { it.contains("\u001e") }.associate {
            val p = it.split("\u001e", limit = 2); p[0] to (p[1].toIntOrNull() ?: 0)
        }.toMutableMap()

    private fun encode(map: Map<String, Int>) = map.entries
        .sortedByDescending { it.value }.take(maxWords)
        .joinToString("\u001f") { "${it.key}\u001e${it.value}" }

    fun learn(text: String) {
        val words = Regex("[\\p{L}\\p{N}']+").findAll(text.lowercase(Locale.ROOT)).map { it.value }.toList()
        if (words.isEmpty() || prefs.getBoolean("incognito", false)) return
        val c = decode(counts)
        val p = decode(pairs)
        words.forEachIndexed { i, word ->
            c[word] = (c[word] ?: 0) + 1
            if (i > 0) { val k = "${words[i - 1]}\u001d$word"; p[k] = (p[k] ?: 0) + 1 }
        }
        prefs.edit().putString(counts, encode(c)).putString(pairs, encode(p)).apply()
    }

    fun suggestions(prefix: String, previous: String = "", limit: Int = 3): List<String> {
        val q = prefix.lowercase(Locale.ROOT)
        val c = decode(counts)
        val p = decode(pairs)
        val fromPair = if (previous.isNotBlank()) p.entries
            .filter { it.key.startsWith(previous.lowercase(Locale.ROOT) + "\u001d") }
            .sortedByDescending { it.value }
            .map { it.key.substringAfter("\u001d") } else emptyList()
        val fromPrefix = c.entries.filter { it.key.startsWith(q) }.sortedByDescending { it.value }.map { it.key }
        return (fromPair + fromPrefix).distinct().take(limit)
    }

    fun clear() { prefs.edit().clear().apply() }
}
