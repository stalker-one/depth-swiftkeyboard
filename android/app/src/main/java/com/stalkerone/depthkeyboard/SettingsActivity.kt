package com.stalkerone.depthkeyboard

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class SettingsActivity : android.app.Activity() {
    companion object {
        const val PREFS = "depth_keyboard"
        const val KEY_SIZE = "size"
        const val KEY_HEIGHT = "height"
        const val KEY_WIDTH = "width"
        const val KEY_COMPACT = "compact"
        const val KEY_LANGUAGE = "language"
        const val KEY_LAYOUT = "layout"
        const val KEY_LAYOUT_MODE = "layout_mode"
        const val KEY_SUGGESTIONS = "suggestions"
        const val KEY_AUTOCORRECT = "autocorrect"
        const val KEY_DOUBLE_SPACE = "double_space"
        const val KEY_FLOW = "flow"
        const val KEY_LONG_PRESS = "long_press"
        const val KEY_NUMBER_ROW = "number_row"
        const val KEY_SPLIT = "split"
        const val KEY_HAPTIC = "haptic"
        const val KEY_SOUND = "sound"
        const val KEY_HIGH_CONTRAST = "high_contrast"
        const val KEY_INCOGNITO = "incognito"
        const val KEY_EMOJI_CATEGORY = "emoji_category"
        const val KEY_THEME = "theme"
        const val KEY_ONE_HANDED = "one_handed"
        const val KEY_FLOATING = "floating"
        const val KEY_AI_ENDPOINT = "ai_endpoint"
        const val KEY_TRANSLATOR_ENDPOINT = "translator_endpoint"
    }

    private val p by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            setBackgroundColor(Color.rgb(15, 23, 42))
        }
        status = TextView(this).apply { setTextColor(Color.WHITE); textSize = 13f }
        root.addView(TextView(this).apply { text = "Depth Keyboard"; setTextColor(Color.WHITE); textSize = 26f })
        root.addView(status)
        addSwitch(root, "Suggestions", KEY_SUGGESTIONS, true)
        addSwitch(root, "Autocorrect", KEY_AUTOCORRECT, true)
        addSwitch(root, "Double-space period", KEY_DOUBLE_SPACE, true)
        addSwitch(root, "Flow typing", KEY_FLOW, false)
        addSwitch(root, "Long-press accents", KEY_LONG_PRESS, true)
        addSwitch(root, "Number row", KEY_NUMBER_ROW, false)
        addSwitch(root, "Split layout", KEY_SPLIT, false)
        addSwitch(root, "Haptic feedback", KEY_HAPTIC, false)
        addSwitch(root, "Key sounds", KEY_SOUND, false)
        addSwitch(root, "High contrast", KEY_HIGH_CONTRAST, false)
        addSwitch(root, "Incognito", KEY_INCOGNITO, false)
        root.addView(select("Language", KEY_LANGUAGE, KeyboardLayouts.languages))
        root.addView(select("Keyboard layout", KEY_LAYOUT, listOf("QWERTY", "QWERTZ", "AZERTY", "DVORAK", "COLEMAK")))
        root.addView(select("Layout mode", KEY_LAYOUT_MODE, listOf("standard", "one-handed", "floating", "split", "tablet", "compact")))
        root.addView(slider("Keyboard size", KEY_SIZE, 80, 140))
        root.addView(slider("Keyboard height", KEY_HEIGHT, 70, 140))
        root.addView(slider("Keyboard width", KEY_WIDTH, 70, 100))
        root.addView(Button(this).apply { text = "Precise sizing"; setOnClickListener { preciseSizing() } })
        root.addView(Button(this).apply { text = "Custom theme"; setOnClickListener { themeEditor() } })
        root.addView(Button(this).apply { text = "AI provider"; setOnClickListener { providerDialog("AI", KEY_AI_ENDPOINT, "https://your-backend.example/ai") } })
        root.addView(Button(this).apply { text = "Translator provider"; setOnClickListener { providerDialog("Translator", KEY_TRANSLATOR_ENDPOINT, "https://your-backend.example/translate") } })
        root.addView(Button(this).apply { text = "Clear learned words"; setOnClickListener { p.edit().remove("learned_words").apply(); Toast.makeText(this@SettingsActivity, "Personalization cleared", Toast.LENGTH_SHORT).show() } })
        root.addView(Button(this).apply { text = "Open Android keyboard settings"; setOnClickListener { startActivity(Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS)) } })
        updateStatus()
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun addSwitch(parent: LinearLayout, label: String, key: String, default: Boolean) {
        val row = LinearLayout(this).apply { setPadding(0, dp(4), 0, dp(4)) }
        val copy = TextView(this).apply { text = label; setTextColor(Color.WHITE); textSize = 15f }
        row.addView(copy, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(Switch(this).apply { isChecked = p.getBoolean(key, default); setOnCheckedChangeListener { _, value -> p.edit().putBoolean(key, value).apply(); updateStatus() } })
        parent.addView(row)
    }

    private fun slider(label: String, key: String, min: Int, max: Int): View {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, dp(4), 0, dp(4)) }
        val value = TextView(this).apply { textSize = 12f; setTextColor(Color.rgb(180, 195, 220)) }
        val bar = SeekBar(this).apply {
            this.max = max - min
            progress = p.getInt(key, 100).coerceIn(min, max) - min
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(v: SeekBar?, n: Int, fromUser: Boolean) { p.edit().putInt(key, n + min).apply(); value.text = "$label  ${n + min}%" }
                override fun onStartTrackingTouch(v: SeekBar?) {}
                override fun onStopTrackingTouch(v: SeekBar?) {}
            })
        }
        value.text = "$label  ${bar.progress + min}%"
        box.addView(value)
        box.addView(bar)
        return box
    }

    private fun select(label: String, key: String, values: List<String>): View {
        val b = Button(this)
        fun refresh() { b.text = "$label: ${p.getString(key, values.first())}" }
        b.setOnClickListener { val current = values.indexOf(p.getString(key, values.first())).coerceAtLeast(0); p.edit().putString(key, values[(current + 1) % values.size]).apply(); refresh(); updateStatus() }
        refresh()
        return b
    }

    private fun providerDialog(title: String, key: String, fallback: String) {
        val input = EditText(this).apply {
            setText(p.getString(key, ""))
            hint = fallback
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        }
        AlertDialog.Builder(this)
            .setTitle("$title provider endpoint")
            .setMessage("Optional. Keep API keys on your backend; do not paste secrets into the APK.")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ -> p.edit().putString(key, input.text.toString().trim()).apply(); Toast.makeText(this, "$title endpoint saved", Toast.LENGTH_SHORT).show() }
            .show()
    }

    private fun themeEditor() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(24), dp(8), dp(24), 0) }
        val bg = colorInput("Background", "custom_bg", "#111827", box)
        val key = colorInput("Key color", "custom_key", "#1F2937", box)
        val text = colorInput("Text color", "custom_text", "#FFFFFF", box)
        AlertDialog.Builder(this).setTitle("Custom theme colors").setMessage("Enter hex colors such as #111827 or #FFFFFF. The keyboard updates when it is reopened.").setView(box).setNegativeButton("Cancel", null).setPositiveButton("Save") { _, _ -> p.edit().putString(KEY_THEME, "custom").putString("custom_bg", bg.text.toString()).putString("custom_key", key.text.toString()).putString("custom_text", text.text.toString()).apply(); Toast.makeText(this, "Custom theme saved", Toast.LENGTH_SHORT).show() }.show()
    }

    private fun preciseSizing() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(24), dp(8), dp(24), 0) }
        val size = numberInput("Overall scale %", KEY_SIZE, 80, 140, box)
        val height = numberInput("Height %", KEY_HEIGHT, 70, 140, box)
        val width = numberInput("Width %", KEY_WIDTH, 70, 100, box)
        AlertDialog.Builder(this).setTitle("Precise keyboard sizing").setMessage("Values are percentages. Changes apply when the keyboard is recreated.").setView(box).setNegativeButton("Cancel", null).setPositiveButton("Save") { _, _ -> p.edit().putInt(KEY_SIZE, size.text.toString().toIntOrNull()?.coerceIn(80, 140) ?: 100).putInt(KEY_HEIGHT, height.text.toString().toIntOrNull()?.coerceIn(70, 140) ?: 100).putInt(KEY_WIDTH, width.text.toString().toIntOrNull()?.coerceIn(70, 100) ?: 100).apply(); Toast.makeText(this, "Exact sizing saved", Toast.LENGTH_SHORT).show() }.show()
    }

    private fun numberInput(label: String, key: String, min: Int, max: Int, parent: LinearLayout): EditText {
        val input = EditText(this).apply { hint = "$label ($min-$max)"; setText(p.getInt(key, 100).toString()); inputType = InputType.TYPE_CLASS_NUMBER; contentDescription = label }
        parent.addView(input)
        return input
    }

    private fun colorInput(label: String, key: String, fallback: String, parent: LinearLayout): EditText {
        val input = EditText(this).apply { hint = label; setText(p.getString(key, fallback)); contentDescription = "$label hex color" }
        parent.addView(input)
        return input
    }

    private fun mode(mode: String) {
        p.edit().putString(KEY_LAYOUT_MODE, mode).putBoolean(KEY_ONE_HANDED, mode == "one-handed").putBoolean(KEY_FLOATING, mode == "floating").apply()
        updateStatus()
        Toast.makeText(this, "Layout mode: $mode", Toast.LENGTH_SHORT).show()
    }

    private fun updateStatus() {
        if (!::status.isInitialized) return
        status.text = "${p.getString(KEY_LANGUAGE, "English (US)")} · ${p.getString(KEY_LAYOUT, "QWERTY")} · ${p.getString(KEY_LAYOUT_MODE, "standard")}"
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
}
