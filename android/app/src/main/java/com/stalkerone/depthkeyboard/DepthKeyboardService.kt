package com.stalkerone.depthkeyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class DepthKeyboardService : InputMethodService() {
    private var shifted = false
    private var symbols = false
    private var tools = false
    private var spanish = false
    private val letters = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
    private val symbolsRows = listOf("1234567890", "@#$%&*+-=", "()[]{}!?/")
    private val emojis = listOf("😀", "😂", "😍", "👍", "🔥", "✨", "🎉", "🤔", "😭", "🙏")

    override fun onCreateInputView(): View = buildKeyboard()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        shifted = false
        symbols = false
        tools = false
    }

    override fun onEvaluateInputViewShown(): Boolean = true

    private fun buildKeyboard(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 12)
            setBackgroundColor(Color.rgb(17, 24, 39))
        }
        val toolbar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        addKey(toolbar, "☺", .8f) { tools = !tools; refresh() }
        addKey(toolbar, "📋", .8f) { pasteClipboard(); refresh() }
        addKey(toolbar, "🌐", .8f) { spanish = !spanish; refresh() }
        addKey(toolbar, "123", .9f) { symbols = !symbols; refresh() }
        val language = TextView(this).apply {
            text = if (spanish) "ES" else "EN"
            setTextColor(Color.rgb(148, 163, 184)); textSize = 11f; gravity = Gravity.CENTER
        }
        toolbar.addView(language, LinearLayout.LayoutParams(0, 42, 1f))
        root.addView(toolbar, LinearLayout.LayoutParams(-1, 48))

        if (tools) {
            val toolRow = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
            emojis.forEach { emoji -> addKey(toolRow, emoji, 1f) { commitText(emoji) } }
            root.addView(toolRow, LinearLayout.LayoutParams(-1, 48))
        } else {
            val suggestions = LinearLayout(this).apply { gravity = Gravity.CENTER }
            val words = if (spanish) listOf("que", "para", "cómo") else listOf("the", "and", "you")
            words.forEach { word -> addKey(suggestions, word, 1f) { commitSuggestion(word) } }
            root.addView(suggestions, LinearLayout.LayoutParams(-1, 48))
        }

        val activeRows = if (symbols) symbolsRows else letters
        activeRows.forEachIndexed { rowIndex, rowText ->
            val row = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
            if (rowIndex == 2) addKey(row, "⇧", .9f) { shifted = !shifted; refresh() }
            rowText.forEach { character ->
                val key = if (shifted && !symbols) character.uppercase() else character.toString()
                addKey(row, key, 1f) {
                    commitText(key)
                    if (shifted && !symbols) { shifted = false; refresh() }
                }
            }
            if (rowIndex == 2) addKey(row, "⌫", 1.2f) { deleteBackwards() }
            root.addView(row, LinearLayout.LayoutParams(-1, 52))
        }

        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER }
        addKey(bottom, if (symbols) "ABC" else "#+=", .9f) { symbols = !symbols; refresh() }
        addKey(bottom, ",", .8f) { commitText(",") }
        addKey(bottom, "space", 4f) { commitText(" ") }
        addKey(bottom, ".", .8f) { commitText(".") }
        addKey(bottom, "↵", 1.1f) { sendEditorAction() }
        root.addView(bottom, LinearLayout.LayoutParams(-1, 58))
        return root
    }

    private fun addKey(row: LinearLayout, label: String, weight: Float, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            textSize = if (label.length > 1) 11f else 18f
            setTextColor(Color.WHITE)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            setOnClickListener { action() }
            background = GradientDrawable().apply { setColor(Color.rgb(31, 41, 55)); cornerRadius = 12f }
        }
        row.addView(button, LinearLayout.LayoutParams(0, -1, weight).apply { setMargins(3, 3, 3, 3) })
    }

    private fun commitText(text: String) { currentInputConnection?.commitText(text, 1) }
    private fun commitSuggestion(word: String) {
        val connection = currentInputConnection ?: return
        connection.commitText("$word ", 1)
    }
    private fun deleteBackwards() {
        val connection = currentInputConnection ?: return
        connection.deleteSurroundingText(1, 0)
    }
    private fun pasteClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) commitText(clip.getItemAt(0).coerceToText(this).toString())
    }
    private fun sendEditorAction() {
        val connection = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: EditorInfo.IME_ACTION_NONE
        if (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED) connection.performEditorAction(action)
        else {
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }
    private fun refresh() { setInputView(buildKeyboard()) }
}
