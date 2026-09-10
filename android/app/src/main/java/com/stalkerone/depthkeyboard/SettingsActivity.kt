package com.stalkerone.depthkeyboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

class SettingsActivity : Activity() {
    companion object {
        const val PREFS = "depth_keyboard_preferences"
        const val KEY_SIZE = "keyboard_size"; const val KEY_WIDTH = "keyboard_width"; const val KEY_HEIGHT = "keyboard_height"
        const val KEY_COMPACT = "compact_layout"; const val KEY_ONE_HANDED = "one_handed"; const val KEY_FLOATING = "floating_layout"; const val KEY_LAYOUT_MODE = "layout_mode"
        const val KEY_DARK_THEME = "dark_theme"; const val KEY_SUGGESTIONS = "suggestions"; const val KEY_INCOGNITO = "incognito"
        const val KEY_LANGUAGE = "language"; const val KEY_LAYOUT = "layout"; const val KEY_THEME = "theme_id"
        const val KEY_DOUBLE_SPACE = "double_space"; const val KEY_AUTOCORRECT = "autocorrect"; const val KEY_LONG_PRESS = "long_press"
        const val KEY_KEY_POPUP = "key_popup"; const val KEY_HAPTIC = "haptic"; const val KEY_SOUND = "sound"; const val KEY_NUMBER_ROW = "number_row"
        const val KEY_FLOW = "flow_typing"; const val KEY_EMOJI_PREDICTIONS = "emoji_predictions"; const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_SPLIT = "split_layout"; const val KEY_EMOJI_CATEGORY = "emoji_category"
        const val KEY_LEARNING = "learning_enabled"; const val KEY_CLIPBOARD = "clipboard_capture"; const val KEY_CLOUD_SYNC = "cloud_sync"
        const val KEY_VOICE = "voice_enabled"; const val KEY_AI_BACKEND = "ai_backend"; const val KEY_TRANSLATOR_BACKEND = "translator_backend"
    }

    private val p by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(content()) }

    private fun content(): View {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(22), dp(22), dp(22), dp(32)); setBackgroundColor(Color.rgb(247, 249, 253)) }
        root.addView(TextView(this).apply { text = "Depth Keyboard"; textSize = 30f; setTextColor(Color.rgb(20, 34, 58)) })
        root.addView(TextView(this).apply { text = "Full local-first keyboard controls"; textSize = 15f; setTextColor(Color.rgb(91, 108, 137)); setPadding(0, dp(4), 0, dp(16)) })
        status = TextView(this).apply { textSize = 13f; setTextColor(Color.rgb(46, 75, 120)); setPadding(dp(14), dp(12), dp(14), dp(12)); setBackgroundColor(Color.rgb(229, 237, 255)) }
        root.addView(status, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })

        section(root, "Smart typing")
        root.addView(slider("Keyboard size", KEY_SIZE, 80, 140)); root.addView(slider("Keyboard height", KEY_HEIGHT, 70, 140)); root.addView(slider("Keyboard width", KEY_WIDTH, 70, 100))
        root.addView(button("Enter exact size values") { preciseSizing() })
        root.addView(toggle("Autocorrect", "Correct common spelling mistakes locally", KEY_AUTOCORRECT, true))
        root.addView(toggle("Next-word predictions", "Use local dictionary and learned words", KEY_SUGGESTIONS, true))
        root.addView(toggle("Double-space punctuation", "Two spaces become period + space", KEY_DOUBLE_SPACE, true))
        root.addView(toggle("Emoji predictions", "Keep recent/favorite emoji available", KEY_EMOJI_PREDICTIONS, true))
        root.addView(toggle("Learn writing style", "Personalize local suggestions from your typing", KEY_LEARNING, true))

        section(root, "Flow, voice & rich input")
        root.addView(toggle("Flow / swipe typing", "Trace letters across the keyboard for a word", KEY_FLOW, false))
        root.addView(toggle("Voice typing", "Enable the microphone/Android speech recognizer entry", KEY_VOICE, true))
        root.addView(toggle("Clipboard capture", "Allow local clipboard history", KEY_CLIPBOARD, true))
        root.addView(button("Emoji, GIFs & stickers") { Toast.makeText(this, "Emoji works offline. GIF/sticker sending requires a configured media provider.", Toast.LENGTH_LONG).show() })
        root.addView(button("Voice input test") { Toast.makeText(this, "Open the keyboard and tap Voice. Android speech recognition handles the microphone UI.", Toast.LENGTH_LONG).show() })

        section(root, "Gestures & key behavior")
        root.addView(toggle("Long-press accents", "Hold a letter for alternate characters", KEY_LONG_PRESS, true))
        root.addView(toggle("Key pop-up preview", "Enable key response preference", KEY_KEY_POPUP, true))
        root.addView(toggle("Haptic feedback", "Vibrate on key press", KEY_HAPTIC, false))
        root.addView(toggle("Key sounds", "Play a key sound", KEY_SOUND, false))

        section(root, "Layouts & languages")
        root.addView(button("Standard layout") { mode("standard") })
        root.addView(button("One-handed layout") { mode("one-handed") })
        root.addView(button("Floating layout") { mode("floating") })
        root.addView(button("Tablet / wide layout") { mode("tablet") })
        root.addView(button("Split / thumb layout") { mode("split") })
        root.addView(toggle("Compact keys", "Reduce key height", KEY_COMPACT, false))
        root.addView(toggle("Number row", "Keep numbers above letters", KEY_NUMBER_ROW, false))
        root.addView(toggle("High contrast", "Increase text/key contrast", KEY_HIGH_CONTRAST, false))
        root.addView(select("Language", KEY_LANGUAGE, KeyboardLayouts.languages))
        root.addView(select("Keyboard layout", KEY_LAYOUT, listOf("QWERTY", "QWERTZ", "AZERTY", "DVORAK", "COLEMAK")))

        section(root, "AI, search & translation")
        root.addView(button("AI assistant") { providerDialog("AI", KEY_AI_BACKEND, "https://your-ai-backend.example/api") })
        root.addView(button("AI tones: Professional / Casual / Polite / Social") { Toast.makeText(this, "Tone controls are exposed in the keyboard toolbar. Generative rewriting needs your configured AI backend.", Toast.LENGTH_LONG).show() })
        root.addView(button("Translator backend") { providerDialog("Translator", KEY_TRANSLATOR_BACKEND, "https://your-translator-backend.example/api") })
        root.addView(button("Search") { Toast.makeText(this, "Keyboard Search opens a web search without sending typing history.", Toast.LENGTH_SHORT).show() })

        section(root, "Themes & appearance")
        root.addView(select("Theme", KEY_THEME, ThemeCatalog.themes.map { it.id } + "custom"))
        root.addView(button("Create / edit custom theme") { themeEditor() })
        root.addView(toggle("Midnight theme", "Use a dark keyboard surface", KEY_DARK_THEME, true))

        section(root, "Privacy, backup & account")
        root.addView(toggle("Incognito mode", "Stop local personalization and clipboard capture", KEY_INCOGNITO, false))
        root.addView(toggle("Cloud sync", "Enable the provider-ready sync preference; no cloud is used by default", KEY_CLOUD_SYNC, false))
        root.addView(button("Account / sync status") { Toast.makeText(this, "Cloud account sync is optional and provider-ready. No credentials are embedded in the APK.", Toast.LENGTH_LONG).show() })
        root.addView(button("Clear learned words") { TypingModel(this).clear(); Toast.makeText(this, "Personalization cleared", Toast.LENGTH_SHORT).show() })
        root.addView(button("Clear clipboard history") { ClipboardStore(this).clear(); Toast.makeText(this, "Clipboard history cleared", Toast.LENGTH_SHORT).show() })
        root.addView(button("Reset all keyboard settings") { p.edit().clear().apply(); Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show(); recreate() })

        section(root, "Setup & support")
        root.addView(button("Enable / choose keyboard") { chooseKeyboard() })
        root.addView(button("Open Android input-method settings") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) })
        root.addView(button("Messaging center") { AlertDialog.Builder(this).setTitle("Depth Keyboard").setMessage("Tips: enable Flow typing for swipe input, use Fix for local spelling/spacing cleanup, use Clipboard for saved snippets, and use Incognito when you do not want personalization. AI/GIF/sticker/cloud features require your own configured provider.").setPositiveButton("OK", null).show() })
        root.addView(TextView(this).apply { text = "Privacy-first design: local typing intelligence is available offline. Network AI, translation, GIF/sticker search and cloud sync are provider hooks and never contain API secrets."; textSize = 12f; setTextColor(Color.rgb(91, 108, 137)); setPadding(0, dp(18), 0, 0) })
        scroll.addView(root); updateStatus(); return scroll
    }

    private fun section(root: LinearLayout, name: String) { root.addView(TextView(this).apply { text = name.uppercase(); textSize = 12f; setTextColor(Color.rgb(82, 103, 144)); setPadding(0, dp(24), 0, dp(8)) }) }
    private fun button(label: String, action: () -> Unit) = Button(this).apply { text = label; isAllCaps = false; setOnClickListener { action() } }
    private fun toggle(label: String, description: String, key: String, default: Boolean): View {
        val box = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(6), 0, dp(6)) }
        val copy = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        copy.addView(TextView(this).apply { text = label; textSize = 15f; setTextColor(Color.rgb(25, 39, 64)) })
        copy.addView(TextView(this).apply { text = description; textSize = 11f; setTextColor(Color.rgb(105, 122, 151)) })
        box.addView(copy, LinearLayout.LayoutParams(0, -2, 1f))
        box.addView(Switch(this).apply { isChecked = p.getBoolean(key, default); setOnCheckedChangeListener { _, value -> p.edit().putBoolean(key, value).apply() } })
        return box
    }
    private fun slider(label: String, key: String, min: Int, max: Int): View {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(4), 0, dp(4)) }
        val value = TextView(this).apply { textSize = 12f; setTextColor(Color.rgb(82, 103, 144)) }
        val bar = SeekBar(this).apply {
            this.max = max - min
            progress = p.getInt(key, 100).coerceIn(min, max) - min
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(v: SeekBar?, n: Int, fromUser: Boolean) { p.edit().putInt(key, n + min).apply(); value.text = "$label  ${n + min}%" }
                override fun onStartTrackingTouch(v: SeekBar?) {}
                override fun onStopTrackingTouch(v: SeekBar?) {}
            })
        }
        value.text = "$label  ${bar.progress + min}%"; box.addView(value); box.addView(bar); return box
    }
    private fun select(label: String, key: String, values: List<String>): View {
        val b = Button(this)
        fun refresh() { b.text = "$label: ${p.getString(key, values.first())}" }
        b.setOnClickListener { val current = values.indexOf(p.getString(key, values.first())).coerceAtLeast(0); p.edit().putString(key, values[(current + 1) % values.size]).apply(); refresh() }
        refresh(); return b
    }
    private fun providerDialog(title: String, key: String, fallback: String) {
        val input = EditText(this).apply { setText(p.getString(key, "")); hint = fallback; setSingleLine(true) }
        AlertDialog.Builder(this).setTitle("$title provider endpoint").setMessage("Optional. Keep API keys on your backend; do not paste secrets into the APK.").setView(input).setNegativeButton("Cancel", null).setPositiveButton("Save") { _, _ -> p.edit().putString(key, input.text.toString().trim()).apply(); Toast.makeText(this, "$title endpoint saved", Toast.LENGTH_SHORT).show() }.show()
    }
    private fun themeEditor() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(24), dp(8), dp(24), 0) }
        val bg = colorInput("Background", "custom_bg", "#111827", box); val key = colorInput("Key color", "custom_key", "#1F2937", box); val text = colorInput("Text color", "custom_text", "#FFFFFF", box)
        AlertDialog.Builder(this).setTitle("Custom theme colors").setMessage("Enter hex colors such as #111827 or #FFFFFF. The keyboard updates when it is reopened.").setView(box).setNegativeButton("Cancel", null).setPositiveButton("Save") { _, _ -> p.edit().putString(KEY_THEME, "custom").putString("custom_bg", bg.text.toString()).putString("custom_key", key.text.toString()).putString("custom_text", text.text.toString()).apply(); Toast.makeText(this, "Custom theme saved", Toast.LENGTH_SHORT).show() }.show()
    }
    private fun preciseSizing() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(24), dp(8), dp(24), 0) }
        val size = numberInput("Overall scale %", KEY_SIZE, 80, 140, box); val height = numberInput("Height %", KEY_HEIGHT, 70, 140, box); val width = numberInput("Width %", KEY_WIDTH, 70, 100, box)
        AlertDialog.Builder(this).setTitle("Precise keyboard sizing").setMessage("Values are percentages. Changes apply when the keyboard is recreated.").setView(box).setNegativeButton("Cancel", null).setPositiveButton("Save") { _, _ -> p.edit().putInt(KEY_SIZE, size.text.toString().toIntOrNull()?.coerceIn(80, 140) ?: 100).putInt(KEY_HEIGHT, height.text.toString().toIntOrNull()?.coerceIn(70, 140) ?: 100).putInt(KEY_WIDTH, width.text.toString().toIntOrNull()?.coerceIn(70, 100) ?: 100).apply(); Toast.makeText(this, "Exact sizing saved", Toast.LENGTH_SHORT).show() }.show()
    }
    private fun numberInput(label: String, key: String, min: Int, max: Int, parent: LinearLayout): EditText { val input = EditText(this).apply { hint = "$label ($min-$max)"; setText(p.getInt(key, 100).toString()); inputType = android.text.InputType.TYPE_CLASS_NUMBER; contentDescription = label }; parent.addView(input); return input }
    private fun colorInput(label: String, key: String, fallback: String, parent: LinearLayout): EditText { val input = EditText(this).apply { hint = label; setText(p.getString(key, fallback)); contentDescription = "$label hex color" }; parent.addView(input); return input }
    private fun mode(mode: String) { p.edit().putString(KEY_LAYOUT_MODE, mode).putBoolean(KEY_ONE_HANDED, mode == "one-handed").putBoolean(KEY_FLOATING, mode == "floating").apply(); updateStatus(); Toast.makeText(this, "Layout mode: $mode", Toast.LENGTH_SHORT).show() }
    private fun chooseKeyboard() { try { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() } catch (_: Throwable) { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } }
    private fun updateStatus() { if (::status.isInitialized) status.text = "Layout: ${p.getString(KEY_LAYOUT_MODE, "standard")}  ·  ${p.getString(KEY_LANGUAGE, "English (US)")}  ·  ${p.getString(KEY_LAYOUT, "QWERTY")}" }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onResume() { super.onResume(); if (::status.isInitialized) updateStatus() }
}
