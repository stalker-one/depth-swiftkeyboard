package com.stalkerone.depthkeyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }
        root.addView(TextView(this).apply {
            text = "Depth Keyboard\n\nA focused, customizable Android keyboard."
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        })
        status = TextView(this).apply {
            gravity = Gravity.CENTER
            textSize = 14f
            setPadding(0, 0, 0, 24)
        }
        root.addView(status)
        root.addView(Button(this).apply {
            text = "Enable Depth Keyboard"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        root.addView(Button(this).apply {
            text = "Choose keyboard"
            setOnClickListener {
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showInputMethodPicker()
                Toast.makeText(this@SettingsActivity, "Choose Depth Keyboard from the picker", Toast.LENGTH_SHORT).show()
            }
        })
        setContentView(root)
        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    private fun updateStatus() {
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_INPUT_METHODS)
            ?.contains(packageName) == true
        status.text = if (enabled) "Depth Keyboard is enabled. Tap Choose keyboard to start typing." else "Depth Keyboard is not enabled yet."
    }
}
