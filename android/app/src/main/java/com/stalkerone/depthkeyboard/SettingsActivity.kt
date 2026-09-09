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
    }

    private val p by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(content()) }

    private fun content(): View {
        val scroll = ScrollView(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(22), dp(22), dp(22), dp(32)); setBackgroundColor(Color.rgb(247, 249, 253)) }
        root.addView(TextView(this).apply { text = "Depth Keyboard"; textSize = 30f; setTextColor(Color.rgb(20, 34, 58)) })
        root.addView(TextView(this).apply { text = "Your keyboard, tuned your way"; textSize = 15f; setTextColor(Color.rgb(91, 108, 137)); setPadding(0, dp(4), 0, dp(16)) })
        status = TextView(this).apply { textSize = 13f; setTextColor(Color.rgb(46, 75, 120)); setPadding(dp(14), dp(12), dp(14), dp(12)); setBackgroundColor(Color.rgb(229, 237, 255)) }
        root.addView(status, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })

        section(root, "Typing")
        root.addView(slider("Keyboard size", KEY_SIZE, 80, 140)); root.addView(slider("Keyboard height", KEY_HEIGHT, 70, 140)); root.addView(slider("Keyboard width", KEY_WIDTH, 70, 100))
        root.addView(button("Enter exact size values") { preciseSizing() })
        root.addView(toggle("Autocorrect", "Correct common spelling mistakes", KEY_AUTOCORRECT, true)); root.addView(toggle("Next-word suggestions", "Show local prediction bar", KEY_SUGGESTIONS, true)); root.addView(toggle("Double-space punctuation", "Two spaces become period + space", KEY_DOUBLE_SPACE, true)); root.addView(toggle("Emoji predictions", "Keep favorite emoji close", KEY_EMOJI_PREDICTIONS, true))

        section(root, "Gestures & key behavior")
        root.addView(toggle("Long-press accents", "Hold a letter for accented characters", KEY_LONG_PRESS, true)); root.addView(toggle("Key pop-up preview", "Show a visual key response", KEY_KEY_POPUP, true)); root.addView(toggle("Flow / swipe typing", "Enable the gesture typing setting", KEY_FLOW, false)); root.addView(toggle("Haptic feedback", "Vibrate on key press", KEY_HAPTIC, false)); root.addView(toggle("Key sounds", "Play a sound on key press", KEY_SOUND, false))

        section(root, "Layout & keys")
        root.addView(button("Standard layout") { mode("standard") }); root.addView(button("One-handed layout") { mode("one-handed") }); root.addView(button("Floating layout") { mode("floating") }); root.addView(toggle("Split / thumb layout", "Keep a comfortable gap in the middle", KEY_SPLIT, false)); root.addView(toggle("Compact keys", "Reduce key height", KEY_COMPACT, false)); root.addView(toggle("Number row", "Keep numbers available above letters", KEY_NUMBER_ROW, false))
        root.addView(select("Language", KEY_LANGUAGE, KeyboardLayouts.languages)); root.addView(select("Keyboard layout", KEY_LAYOUT, listOf("QWERTY", "QWERTZ", "AZERTY", "DVORAK", "COLEMAK")))

        section(root, "Rich input & toolbar")
        root.addView(button("Emoji, GIFs & stickers") { Toast.makeText(this, "Emoji is offline; GIF/sticker providers can be configured later", Toast.LENGTH_SHORT).show() }); root.addView(button("Clipboard history") { Toast.makeText(this, "Up to 50 clips are stored locally", Toast.LENGTH_SHORT).show() }); root.addView(button("AI tone / rewrite") { Toast.makeText(this, "Local rewrite prompt starter is available from the toolbar", Toast.LENGTH_SHORT).show() }); root.addView(button("Search & translator") { Toast.makeText(this, "Search opens your browser; translator is provider-ready", Toast.LENGTH_SHORT).show() })

        section(root, "Themes & accessibility")
        root.addView(select("Theme", KEY_THEME, ThemeCatalog.themes.map { it.id } + "custom")); root.addView(button("Create / edit custom theme") { themeEditor() }); root.addView(toggle("High contrast", "Increase visual separation between keys", KEY_HIGH_CONTRAST, false)); root.addView(toggle("Midnight theme", "Use a dark keyboard surface", KEY_DARK_THEME, true))

        section(root, "Privacy & data")
        root.addView(toggle("Incognito mode", "Stop local personalization and clipboard capture", KEY_INCOGNITO, false)); root.addView(button("Clear learned words") { TypingModel(this).clear(); Toast.makeText(this, "Personalization cleared", Toast.LENGTH_SHORT).show() }); root.addView(button("Clear clipboard history") { ClipboardStore(this).clear(); Toast.makeText(this, "Clipboard history cleared", Toast.LENGTH_SHORT).show() }); root.addView(button("Reset all keyboard settings") { p.edit().clear().apply(); Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show(); recreate() })

        section(root, "Setup")
        root.addView(button("Enable / choose keyboard") { chooseKeyboard() }); root.addView(button("Open Android input-method settings") { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) })
        root.addView(TextView(this).apply { text = "Depth Keyboard is privacy-first. Typing personalization and clipboard history stay on this device unless you connect your own provider."; textSize = 12f; setTextColor(Color.rgb(91, 108, 137)); setPadding(0, dp(18), 0, 0) })
        scroll.addView(root); updateStatus(); return scroll
    }

    private fun section(root: LinearLayout, name: String) { root.addView(TextView(this).apply { text = name.uppercase(); textSize = 12f; setTextColor(Color.rgb(82, 103, 144)); setPadding(0, dp(24), 0, dp(8)) }) }
    private fun button(label: String, action: () -> Unit) = Button(this).apply { text = label; isAllCaps = false; setOnClickListener { action() } }
    private fun toggle(label: String, description: String, key: String, default: Boolean): View { val box = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(6), 0, dp(6)) }; val copy = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }; copy.addView(TextView(this).apply { text = label; textSize = 15f; setTextColor(Color.rgb(25, 39, 64)) }); copy.addView(TextView(this).apply { text = description; textSize = 11f; setTextColor(Color.rgb(105, 122, 151)) }); box.addView(copy, LinearLayout.LayoutParams(0, -2, 1f)); box.addView(Switch(this).apply { isChecked = p.getBoolean(key, default); setOnCheckedChangeListener { _, value -> p.edit().putBoolean(key, value).apply() } }); return box }
    private fun slider(label: String, key: String, min: Int, max: Int): View { val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(4), 0, dp(4)) }; val value = TextView(this).apply { textSize = 12f; setTextColor(Color.rgb(82, 103, 144)) }; val bar = SeekBar(this).apply { this.max = max - min; progress = p.getInt(key, 100).coerceIn(min, max) - min; setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { override fun onProgressChanged(v: SeekBar?, n: Int, fromUser: Boolean) { p.edit().putInt(key, n + min).apply(); value.text = "$label  ${n + min}%" }; override fun onStartTrackingTouch(v: SeekBar?) {} ; override fun onStopTrackingTouch(v: SeekBar?) {} }) }; value.text = "$label  ${bar.progress + min}%"; box.addView(value); box.addView(bar); return box }
    private fun select(label: String, key: String, values: List<String>): View { val b = Button(this); fun refresh() { b.text = "$label: ${p.getString(key, values.first())}" }; b.setOnClickListener { val current = values.indexOf(p.getString(key, values.first())).coerceAtLeast(0); p.edit().putString(key, values[(current + 1) % values.size]).apply(); refresh() }; refresh(); return b }
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
    private fun mode(mode: String) { p.edit().putString(KEY_LAYOUT_MODE, mode).putBoolean(KEY_ONE_HANDED, mode == "one-handed").putBoolean(KEY_FLOATING, mode == "floating").apply(); updateStatus() }
    private fun chooseKeyboard() { try { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() } catch (_: Throwable) { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } }
    private fun updateStatus() { if (::status.isInitialized) status.text = "Layout: ${p.getString(KEY_LAYOUT_MODE, "standard")}  ·  ${p.getString(KEY_LANGUAGE, "English (US)")}  ·  ${p.getString(KEY_LAYOUT, "QWERTY")}" }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    override fun onResume() { super.onResume(); if (::status.isInitialized) updateStatus() }
}
