package com.stalkerone.depthkeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

class SettingsActivity : Activity() {
    companion object {
        const val PREFS = "depth_keyboard_preferences"
        const val KEY_SIZE = "keyboard_size"
        const val KEY_COMPACT = "compact_layout"
        const val KEY_ONE_HANDED = "one_handed"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_SUGGESTIONS = "suggestions"
        const val KEY_INCOGNITO = "incognito"
    }

    private lateinit var status: TextView
    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try { setContentView(buildContent()); updateStatus() } catch (_: Throwable) {
            setContentView(TextView(this).apply { text = "Depth Keyboard\n\nOpen Android Settings to enable the keyboard."; textSize = 20f; gravity = Gravity.CENTER; setPadding(32, 32, 32, 32) })
        }
    }

    private fun buildContent(): View {
        val scroll = android.widget.ScrollView(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 24, 28, 28) }
        root.addView(TextView(this).apply { text = "Depth Keyboard"; textSize = 28f; setTextColor(0xff18243a.toInt()) })
        root.addView(TextView(this).apply { text = "Personalize your typing experience"; textSize = 14f; setTextColor(0xff71809b.toInt()); setPadding(0, 4, 0, 18) })
        status = TextView(this).apply { textSize = 13f; setPadding(14, 12, 14, 12); setBackgroundColor(0xffeef2ff.toInt()) }
        root.addView(status, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 18 })

        root.addView(sectionTitle("Keyboard size"))
        val sizeValue = TextView(this).apply { textSize = 13f; setTextColor(0xff5669d5.toInt()) }
        val sizeBar = SeekBar(this).apply { max = 60; progress = prefs.getInt(KEY_SIZE, 100).coerceIn(80, 140) - 80 }
        fun updateSizeLabel() { sizeValue.text = "${sizeBar.progress + 80}%" }
        sizeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, value: Int, fromUser: Boolean) { prefs.edit().putInt(KEY_SIZE, value + 80).apply(); updateSizeLabel() }
            override fun onStartTrackingTouch(bar: SeekBar?) = Unit
            override fun onStopTrackingTouch(bar: SeekBar?) = Unit
        })
        val sizeRow = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        sizeRow.addView(sizeBar, LinearLayout.LayoutParams(0, -2, 1f)); sizeRow.addView(sizeValue, LinearLayout.LayoutParams(50, -2))
        root.addView(sizeRow)
        updateSizeLabel()

        root.addView(sectionTitle("Layout & appearance"))
        root.addView(settingSwitch("Compact keys", "Reduce key height for more content", KEY_COMPACT, false))
        root.addView(settingSwitch("One-handed mode", "Keep the keyboard closer to your thumb", KEY_ONE_HANDED, false))
        root.addView(settingSwitch("Midnight theme", "Use a dark keyboard surface", KEY_DARK_THEME, true))
        root.addView(settingSwitch("Smart suggestions", "Show next-word suggestions above the keys", KEY_SUGGESTIONS, true))
        root.addView(settingSwitch("Incognito mode", "Do not personalize suggestions in this session", KEY_INCOGNITO, false))

        root.addView(sectionTitle("Languages & tools"))
        root.addView(TextView(this).apply { text = "English (US)  ·  Español (ES)\nEmoji  ·  Clipboard  ·  Symbols  ·  Translator-ready"; textSize = 14f; setTextColor(0xff50617c.toInt()); setPadding(0, 0, 0, 12) })
        root.addView(Button(this).apply { text = "Manage languages"; setOnClickListener { Toast.makeText(this@SettingsActivity, "English and Español are enabled", Toast.LENGTH_SHORT).show() } })
        root.addView(Button(this).apply { text = "Enable Depth Keyboard"; setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } })
        root.addView(Button(this).apply { text = "Choose keyboard"; setOnClickListener { chooseKeyboard() } })
        root.addView(TextView(this).apply { text = "Changes apply the next time the keyboard opens. Tap the ⚙ toolbar button to return here."; textSize = 12f; setTextColor(0xff71809b.toInt()); setPadding(0, 18, 0, 0) })
        scroll.addView(root)
        return scroll
    }

    private fun sectionTitle(text: String) = TextView(this).apply { this.text = text; textSize = 17f; setTextColor(0xff18243a.toInt()); setPadding(0, 22, 0, 8) }

    private fun settingSwitch(title: String, subtitle: String, key: String, default: Boolean): View {
        val box = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(0, 8, 0, 8) }
        val copy = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        copy.addView(TextView(this).apply { text = title; textSize = 14f; setTextColor(0xff18243a.toInt()) })
        copy.addView(TextView(this).apply { text = subtitle; textSize = 11f; setTextColor(0xff71809b.toInt()) })
        box.addView(copy, LinearLayout.LayoutParams(0, -2, 1f))
        val toggle = Switch(this).apply { isChecked = prefs.getBoolean(key, default); setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean(key, checked).apply() } }
        box.addView(toggle)
        return box
    }

    private fun chooseKeyboard() {
        try { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker(); Toast.makeText(this, "Choose Depth Keyboard from the picker", Toast.LENGTH_SHORT).show() }
        catch (_: Throwable) { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
    }

    override fun onResume() { super.onResume(); if (::status.isInitialized) updateStatus() }
    private fun updateStatus() {
        status.text = try { val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_INPUT_METHODS)?.split(':')?.any { it.startsWith("$packageName/") } == true; if (enabled) "✓ Depth Keyboard is enabled" else "Depth Keyboard is not enabled yet" } catch (_: Throwable) { "Use Enable Depth Keyboard below to finish setup." }
    }
}
