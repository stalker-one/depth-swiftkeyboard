package com.stalkerone.depthkeyboard

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(32, 32, 32, 32) }
        root.addView(TextView(this).apply { text = "Depth Keyboard\n\nA focused, customizable Android keyboard."; textSize = 24f; gravity = Gravity.CENTER; setPadding(0, 0, 0, 32) })
        root.addView(Button(this).apply { text = "Enable Depth Keyboard"; setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) } })
        root.addView(Button(this).apply { text = "Choose keyboard"; setOnClickListener { (getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager).showInputMethodPicker() } })
        setContentView(root)
    }
}
