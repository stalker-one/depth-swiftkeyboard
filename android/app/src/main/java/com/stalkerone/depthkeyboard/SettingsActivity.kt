package com.stalkerone.depthkeyboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class SettingsActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(buildContent())
            updateStatus()
        } catch (_: Throwable) {
            setContentView(TextView(this).apply {
                text = "Depth Keyboard\n\nOpen Android Settings to enable the keyboard."
                textSize = 20f
                gravity = Gravity.CENTER
                setPadding(32, 32, 32, 32)
            })
        }
    }

    private fun buildContent(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(32, 32, 32, 32)
        addView(TextView(this@SettingsActivity).apply {
            text = "Depth Keyboard\n\nA focused, customizable Android keyboard."
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        })
        status = TextView(this@SettingsActivity).apply {
            gravity = Gravity.CENTER
            textSize = 14f
            setPadding(0, 0, 0, 24)
        }
        addView(status)
        addView(Button(this@SettingsActivity).apply {
            text = "Enable Depth Keyboard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        addView(Button(this@SettingsActivity).apply {
            text = "Choose keyboard"
            setOnClickListener {
                try {
                    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.showInputMethodPicker()
                    Toast.makeText(this@SettingsActivity, "Choose Depth Keyboard from the picker", Toast.LENGTH_SHORT).show()
                } catch (_: Throwable) {
                    startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    private fun updateStatus() {
        status.text = try {
            val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_INPUT_METHODS)
                ?.split(':')?.any { it.startsWith("$packageName/") } == true
            if (enabled) "Depth Keyboard is enabled. Tap Choose keyboard to start typing." else "Depth Keyboard is not enabled yet."
        } catch (_: Throwable) {
            "Use Enable Depth Keyboard below to finish setup."
        }
    }
}
