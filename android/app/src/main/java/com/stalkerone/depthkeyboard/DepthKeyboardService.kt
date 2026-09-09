package com.stalkerone.depthkeyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class DepthKeyboardService : InputMethodService() {
    private var shifted = false
    private var symbols = false
    private var tools = false
    private var spanish = false

    private val letters = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")
    private val symbolsRows = listOf("1234567890", "@#$%&*+-=", "()[]{}!?/")
    private val emojis = listOf("😀", "😂", "😍", "👍", "🔥", "✨", "🎉", "🤔", "😭", "🙏")

    private val prefs get() = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE)
    private val sizeScale get() = prefs.getInt(SettingsActivity.KEY_SIZE, 100) / 100f
    private val widthScale get() = prefs.getInt(SettingsActivity.KEY_WIDTH, 100) / 100f
    private val heightScale get() = prefs.getInt(SettingsActivity.KEY_HEIGHT, 100) / 100f
    private val compact get() = prefs.getBoolean(SettingsActivity.KEY_COMPACT, false)
    private val oneHanded get() = prefs.getBoolean(SettingsActivity.KEY_ONE_HANDED, false)
    private val floating get() = prefs.getBoolean(SettingsActivity.KEY_FLOATING, false)
    private val darkTheme get() = prefs.getBoolean(SettingsActivity.KEY_DARK_THEME, true)
    private val suggestionsEnabled get() = prefs.getBoolean(SettingsActivity.KEY_SUGGESTIONS, true)

    override fun onCreateInputView(): View = buildKeyboard()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        shifted = false
        symbols = false
        tools = false
    }

    override fun onEvaluateInputViewShown(): Boolean = true

    private fun buildKeyboard(): View {
        val background = if (darkTheme) Color.rgb(17, 24, 39) else Color.rgb(244, 247, 251)
        val foreground = if (darkTheme) Color.WHITE else Color.rgb(24, 36, 58)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = when {
                oneHanded -> Gravity.END
                else -> Gravity.CENTER
            }
            setPadding(dp(6), dp(4), dp(6), dp(8))
            setBackgroundColor(background)
        }

        val screenWidth = resources.displayMetrics.widthPixels
        val normalWidth = (screenWidth * widthScale).roundToInt().coerceAtMost(screenWidth)
        val contentWidth = when {
            floating -> (screenWidth * (widthScale * 0.88f)).roundToInt().coerceIn(dp(220), screenWidth)
            oneHanded -> (screenWidth * (widthScale * 0.82f)).roundToInt().coerceIn(dp(220), screenWidth)
            else -> normalWidth
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(background)
        }
        root.addView(content, LinearLayout.LayoutParams(contentWidth, ViewGroup.LayoutParams.WRAP_CONTENT))

        val toolbar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        addKey(toolbar, "☰", .8f, foreground) { tools = !tools; refresh() }
        addKey(toolbar, "📋", .8f, foreground) { pasteClipboard() }
        addKey(toolbar, "🌐", .8f, foreground) { spanish = !spanish; refresh() }
        addKey(toolbar, "123", .9f, foreground) { symbols = !symbols; refresh() }
        addKey(toolbar, "↕", .8f, foreground) { cycleHeight() }
        addKey(toolbar, "↔", .8f, foreground) { cycleWidth() }
        val language = TextView(this).apply {
            text = if (spanish) "ES" else "EN"
            setTextColor(if (darkTheme) Color.rgb(148, 163, 184) else Color.DKGRAY)
            textSize = 11f
            gravity = Gravity.CENTER
        }
        toolbar.addView(language, LinearLayout.LayoutParams(0, scaledHeight(42), 1f))
        addKey(toolbar, "⚙", .8f, foreground) {
            startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        content.addView(toolbar, LinearLayout.LayoutParams(-1, scaledHeight(48)))

        if (tools) {
            val toolRow = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
            listOf("Emoji", "GIFs", "Tone", "Search", "Translate", "Layout").forEach { tool ->
                addKey(toolRow, tool, 1f, foreground) {
                    when (tool) {
                        "Emoji" -> emojis.take(5).forEach { commitText(it) }
                        "Tone" -> commitText("Please rewrite this in a friendly tone: ")
                        "Translate" -> commitText("Translation: ")
                        "Search" -> commitText("Search: ")
                        "GIFs" -> Toast.makeText(this, "GIF provider can be connected in the AI tools update", Toast.LENGTH_SHORT).show()
                        "Layout" -> startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                }
            }
            content.addView(toolRow, LinearLayout.LayoutParams(-1, scaledHeight(48)))
        } else if (suggestionsEnabled) {
            val suggestions = LinearLayout(this).apply { gravity = Gravity.CENTER }
            val words = if (spanish) listOf("que", "para", "cómo") else listOf("the", "and", "you")
            words.forEach { word -> addKey(suggestions, word, 1f, foreground) { commitSuggestion(word) } }
            content.addView(suggestions, LinearLayout.LayoutParams(-1, scaledHeight(48)))
        }

        val activeRows = if (symbols) symbolsRows else letters
        activeRows.forEachIndexed { rowIndex, rowText ->
            val row = LinearLayout(this).apply { gravity = Gravity.CENTER; orientation = LinearLayout.HORIZONTAL }
            if (rowIndex == 2) addKey(row, "⇧", .9f, foreground) { shifted = !shifted; refresh() }
            rowText.forEach { character ->
                val key = if (shifted && !symbols) character.uppercase() else character.toString()
                addKey(row, key, 1f, foreground) {
                    commitText(key)
                    if (shifted && !symbols) { shifted = false; refresh() }
                }
            }
            if (rowIndex == 2) addKey(row, "⌫", 1.2f, foreground) { deleteBackwards() }
            content.addView(row, LinearLayout.LayoutParams(-1, scaledHeight(if (compact) 44 else 52)))
        }

        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER }
        addKey(bottom, if (symbols) "ABC" else "#+=", .9f, foreground) { symbols = !symbols; refresh() }
        addKey(bottom, ",", .8f, foreground) { commitText(",") }
        addKey(bottom, "space", 4f, foreground) { commitText(" ") }
        addKey(bottom, ".", .8f, foreground) { commitText(".") }
        addKey(bottom, "↵", 1.1f, foreground) { sendEditorAction() }
        content.addView(bottom, LinearLayout.LayoutParams(-1, scaledHeight(58)))
        return root
    }

    private fun scaledHeight(value: Int): Int = dp(value * sizeScale * heightScale)
    private fun keyTextSize(label: String): Float = (if (label.length > 1) 11f else 18f) * sizeScale

    private fun addKey(row: LinearLayout, label: String, weight: Float, foreground: Int, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            textSize = keyTextSize(label)
            setTextColor(foreground)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            minHeight = 0
            minWidth = 0
            setOnClickListener { action() }
            background = GradientDrawable().apply {
                setColor(if (darkTheme) Color.rgb(31, 41, 55) else Color.WHITE)
                cornerRadius = dp(10).toFloat()
            }
        }
        row.addView(button, LinearLayout.LayoutParams(0, scaledHeight(if (compact) 44 else 52), weight).apply {
            setMargins(dp(2), dp(2), dp(2), dp(2))
        })
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
    private fun dp(value: Float): Int = (value * resources.displayMetrics.density).roundToInt()

    private fun cycleHeight() {
        val current = prefs.getInt(SettingsActivity.KEY_HEIGHT, 100)
        val next = when {
            current >= 140 -> 70
            else -> current + 10
        }
        prefs.edit().putInt(SettingsActivity.KEY_HEIGHT, next).apply()
        Toast.makeText(this, "Keyboard height: $next%", Toast.LENGTH_SHORT).show()
        refresh()
    }

    private fun cycleWidth() {
        val current = prefs.getInt(SettingsActivity.KEY_WIDTH, 100)
        val next = when {
            current >= 100 -> 70
            else -> current + 10
        }
        prefs.edit().putInt(SettingsActivity.KEY_WIDTH, next).apply()
        Toast.makeText(this, "Keyboard width: $next%", Toast.LENGTH_SHORT).show()
        refresh()
    }

    private fun commitText(text: String) { currentInputConnection?.commitText(text, 1) }
    private fun commitSuggestion(word: String) { currentInputConnection?.commitText("$word ", 1) }
    private fun deleteBackwards() { currentInputConnection?.deleteSurroundingText(1, 0) }

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