package com.stalkerone.depthkeyboard

import android.content.Context
import android.graphics.Color

object ThemeCatalog {
    data class Theme(val id: String, val background: Int, val key: Int, val text: Int)
    val themes = listOf(
        Theme("midnight", Color.rgb(17,24,39), Color.rgb(31,41,55), Color.WHITE),
        Theme("snow", Color.rgb(245,247,250), Color.WHITE, Color.rgb(24,36,58)),
        Theme("ocean", Color.rgb(10,38,60), Color.rgb(18,70,95), Color.WHITE),
        Theme("forest", Color.rgb(15,43,31), Color.rgb(26,76,51), Color.WHITE),
        Theme("violet", Color.rgb(39,25,60), Color.rgb(72,44,105), Color.WHITE)
    )
    fun current(context: Context): Theme {
        val prefs = context.getSharedPreferences(SettingsActivity.PREFS, Context.MODE_PRIVATE)
        val id = prefs.getString("theme_id", "midnight")
        if (id == "custom") {
            fun color(key: String, fallback: Int) = runCatching { Color.parseColor(prefs.getString(key, null) ?: "") }.getOrDefault(fallback)
            return Theme("custom", color("custom_bg", themes.first().background), color("custom_key", themes.first().key), color("custom_text", themes.first().text))
        }
        return themes.firstOrNull { it.id == id } ?: themes.first()
    }
}
