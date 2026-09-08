package com.stalkerone.depthkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

class DepthKeyboardService : InputMethodService() {
    private var shifted = false
    private var symbols = false
    private val rows = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
    private val symbolRows = listOf("1234567890", "@#$%&*+-=", "()[]{}!?/")

    override fun onCreateInputView(): View {
        return buildKeyboard()
    }

    private fun buildKeyboard(): View {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(8, 8, 8, 12); setBackgroundColor(Color.rgb(17, 24, 39)) }
        val suggestions = TextView(this).apply { text = "the     to     your     and"; textSize = 16f; setTextColor(Color.rgb(226, 232, 240)); gravity = 17; setPadding(8, 18, 8, 18) }
        root.addView(suggestions, LinearLayout.LayoutParams(-1, -2))
        val activeRows = if (symbols) symbolRows else rows
        activeRows.forEachIndexed { rowIndex, letters ->
            val row = LinearLayout(this).apply { gravity = 17; orientation = LinearLayout.HORIZONTAL }
            if (rowIndex == 2) addKey(row, "⇧", 0.9f) { shifted = !shifted; refresh() }
            letters.forEach { char -> addKey(row, if (shifted) char.uppercase() else char.toString(), 1f) { commitText(if (shifted) char.uppercase() else char.toString()); if (shifted) { shifted = false; refresh() } } }
            if (rowIndex == 2) addKey(row, "⌫", 1.2f) { currentInputConnection?.deleteSurroundingText(1, 0) }
            root.addView(row, LinearLayout.LayoutParams(-1, 52))
        }
        val bottom = LinearLayout(this).apply { gravity = 17 }
        addKey(bottom, "☻", 0.9f) { commitText("🙂") }
        addKey(bottom, "🌐", 0.9f) { switchToNextInputMethod(false) }
        addKey(bottom, "space", 4f) { commitText(" ") }
        addKey(bottom, "↵", 1.1f) { currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)); currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)) }
        root.addView(bottom, LinearLayout.LayoutParams(-1, 58))
        return root
    }

    private fun addKey(row: LinearLayout, label: String, weight: Float, action: () -> Unit) {
        val button = Button(this).apply { text = label; textSize = if (label.length > 1) 12f else 18f; setTextColor(Color.WHITE); setOnClickListener { action() }; background = GradientDrawable().apply { setColor(Color.rgb(31, 41, 55)); cornerRadius = 12f } }
        row.addView(button, LinearLayout.LayoutParams(0, -1, weight).apply { setMargins(3, 3, 3, 3) })
    }

    private fun commitText(text: String) { currentInputConnection?.commitText(text, 1) }
    private fun refresh() { setInputView(buildKeyboard()) }
}
