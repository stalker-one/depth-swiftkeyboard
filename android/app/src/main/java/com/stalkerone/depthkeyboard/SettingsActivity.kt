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

class SettingsActivity : Activity() {
    companion object {
        const val PREFS = "depth_keyboard_preferences"
        const val KEY_SIZE = "keyboard_size"
        const val KEY_WIDTH = "keyboard_width"
        const val KEY_HEIGHT = "keyboard_height"
        const val KEY_COMPACT = "compact_layout"
        const val KEY_ONE_HANDED = "one_handed"
        const val KEY_FLOATING = "floating_layout"
        const val KEY_LAYOUT_MODE = "layout_mode"
        const val KEY_DARK_THEME = "dark_theme"
        const val KEY_SUGGESTIONS = "suggestions"
        const val KEY_INCOGNITO = "incognito"
    }

    private lateinit var status: TextView
    private val prefs by lazy { getSharedPreferences(PREFS, MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildContent())
        updateStatus()
    }

    private fun buildContent(): View {
        val scroll = android.widget.ScrollView(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 24, 28, 28) }

        root.addView(TextView(this).apply {
            text = "Depth Keyboard"; textSize = 28f; setTextColor(0xff18243a.toInt())
        })
        root.addView(TextView(this).apply {
            text = "Keyboard layout, height and width"; textSize = 14f; setTextColor(0xff71809b.toInt()); setPadding(0, 4, 0, 18)
        })
        status = TextView(this).apply { textSize = 13f; setPadding(14, 12, 14, 12); setBackgroundColor(0xffeef2ff.toInt()) }
        root.addView(status, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 18 })

        root.addView(sectionTitle("Keyboard size"))
        root.addView(sizeControl("Overall key size", KEY_SIZE, 80, 140, 100))
        root.addView(sizeControl("Keyboard height", KEY_HEIGHT, 70, 140, 100))
        root.addView(sizeControl("Keyboard width", KEY_WIDTH, 70, 100, 100))

        root.addView(sectionTitle("Layout mode"))
        root.addView(layoutButton("Standard", "Full-width keyboard", "standard"))
        root.addView(layoutButton("One-handed", "Move keyboard to the right side", "one-handed"))
        root.addView(layoutButton("Floating", "Use a narrower floating-style keyboard", "floating"))
        root.addView(Button(this).apply {
            text = "Reset layout size"
            setOnClickListener {
                prefs.edit().putInt(KEY_SIZE, 100).putInt(KEY_WIDTH, 100).putInt(KEY_HEIGHT, 100)
                    .putBoolean(KEY_ONE_HANDED, false).putBoolean(KEY_FLOATING, false).putString(KEY_LAYOUT_MODE, "standard").apply()
                recreate()
            }
        })

        root.addView(sectionTitle("Layout & appearance"))
        root.addView(settingSwitch("Compact keys", "Reduce key height for more content", KEY_COMPACT, false))
        root.addView(settingSwitch("One-handed mode", "Keep the keyboard closer to your thumb", KEY_ONE_HANDED, false))
        root.addView(settingSwitch("Floating mode", "Use a centered narrow keyboard", KEY_FLOATING, false))
        root.addView(settingSwitch("Midnight theme", "Use a dark keyboard surface", KEY_DARK_THEME, true))
        root.addView(settingSwitch("Smart suggestions", "Show next-word suggestions above the keys", KEY_SUGGESTIONS, true))
        root.addView(settingSwitch("Incognito mode", "Do not personalize suggestions in this session", KEY_INCOGNITO, false))

        root.addView(sectionTitle("Device keyboard settings"))
        root.addView(Button(this).apply {
            text = "Open Android keyboard settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        root.addView(Button(this).apply {
            text = "Choose Depth Keyboard now"
            setOnClickListener { chooseKeyboard() }
        })

        root.addView(sectionTitle("Languages & tools"))
        root.addView(TextView(this).apply {
            text = "English (US)  ·  Español (ES)\nEmoji  ·  Clipboard  ·  Symbols  ·  AI-ready  ·  Translator-ready"
            textSize = 14f; setTextColor(0xff50617c.toInt()); setPadding(0, 0, 0, 12)
        })
        root.addView(Button(this).apply { text = "Manage languages" })
        root.addView(TextView(this).apply {
            text = "Changes are saved immediately and are applied when the keyboard redraws. The keyboard toolbar also has quick resize controls."
            textSize = 12f; setTextColor(0xff71809b.toInt()); setPadding(0, 18, 0, 0)
        })

        scroll.addView(root)
        return scroll
    }

    private fun sizeControl(title: String, key: String, min: Int, max: Int, default: Int): View {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(0, 8, 0, 10) }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        val label = TextView(this).apply { text = title; textSize = 14f; setTextColor(0xff18243a.toInt()) }
        val value = TextView(this).apply { textSize = 13f; setTextColor(0xff5669d5.toInt()); gravity = Gravity.END }
        row.addView(label, LinearLayout.LayoutParams(0, -2, 1f)); row.addView(value, LinearLayout.LayoutParams(60, -2))
        box.addView(row)
        val bar = SeekBar(this).apply {
            this.max = max - min
            progress = (prefs.getInt(key, default).coerceIn(min, max) - min)
        }
        fun update() { value.text = "${bar.progress + min}%" }
        bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(b: SeekBar?, progress: Int, fromUser: Boolean) {
                prefs.edit().putInt(key, progress + min).apply(); update()
            }
            override fun onStartTrackingTouch(b: SeekBar?) = Unit
            override fun onStopTrackingTouch(b: SeekBar?) = Unit
        })
        box.addView(bar)
        update()
        return box
    }

    private fun layoutButton(title: String, subtitle: String, mode: String): View {
        return Button(this).apply {
            text = "$title\n$subtitle"
            isAllCaps = false
            setOnClickListener {
                prefs.edit().putString(KEY_LAYOUT_MODE, mode)
                    .putBoolean(KEY_ONE_HANDED, mode == "one-handed")
                    .putBoolean(KEY_FLOATING, mode == "floating").apply()
                recreate()
            }
        }
    }

    private fun sectionTitle(text: String) = TextView(this).apply {
        this.text = text; textSize = 17f; setTextColor(0xff18243a.toInt()); setPadding(0, 22, 0, 8)
    }

    private fun settingSwitch(title: String, subtitle: String, key: String, default: Boolean): View {
        val box = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL; setPadding(0, 8, 0, 8) }
        val copy = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        copy.addView(TextView(this).apply { text = title; textSize = 14f; setTextColor(0xff18243a.toInt()) })
        copy.addView(TextView(this).apply { text = subtitle; textSize = 11f; setTextColor(0xff71809b.toInt()) })
        box.addView(copy, LinearLayout.LayoutParams(0, -2, 1f))
        val toggle = Switch(this).apply {
            isChecked = prefs.getBoolean(key, default)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean(key, checked).apply()
                if (key == KEY_ONE_HANDED && checked) prefs.edit().putBoolean(KEY_FLOATING, false).putString(KEY_LAYOUT_MODE, "one-handed").apply()
                if (key == KEY_FLOATING && checked) prefs.edit().putBoolean(KEY_ONE_HANDED, false).putString(KEY_LAYOUT_MODE, "floating").apply()
            }
        }
        box.addView(toggle)
        return box
    }

    private fun chooseKeyboard() {
        try { (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker() }
        catch (_: Throwable) { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
    }

    override fun onResume() { super.onResume(); if (::status.isInitialized) updateStatus() }

    private fun updateStatus() {
        status.text = try {
            val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_INPUT_METHODS)
                ?.split(':')?.any { it.startsWith("$packageName/") } == true
            val mode = prefs.getString(KEY_LAYOUT_MODE, "standard") ?: "standard"
            val width = prefs.getInt(KEY_WIDTH, 100)
            val height = prefs.getInt(KEY_HEIGHT, 100)
            if (enabled) "✓ Enabled · $mode · width ${width}% · height ${height}%" else "Keyboard not enabled · choose Enable below"
        } catch (_: Throwable) { "Use the Android keyboard settings below to finish setup." }
    }
}