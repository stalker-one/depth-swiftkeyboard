package com.stalkerone.depthkeyboard

import android.content.Intent
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
import android.widget.Toast
import kotlin.math.roundToInt

class DepthKeyboardService : InputMethodService() {
    private var shift = false
    private var symbols = false
    private var tools = false
    private lateinit var model: TypingModel

    private val p get() = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE)
    private val scale get() = p.getInt(SettingsActivity.KEY_SIZE, 100) / 100f
    private val height get() = p.getInt(SettingsActivity.KEY_HEIGHT, 100) / 100f
    private val compact get() = p.getBoolean(SettingsActivity.KEY_COMPACT, false)
    private val dark get() = p.getBoolean(SettingsActivity.KEY_DARK_THEME, true)

    override fun onCreate() {
        super.onCreate()
        model = TypingModel(this)
    }

    override fun onCreateInputView(): View = build()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        shift = false
        symbols = false
        tools = false
    }

    override fun onEvaluateInputViewShown() = true

    private fun build(): View {
        val bg = if (dark) Color.rgb(17, 24, 39) else Color.rgb(245, 247, 250)
        val fg = if (dark) Color.WHITE else Color.rgb(24, 36, 58)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(3), dp(5), dp(7))
            setBackgroundColor(bg)
        }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val width = (resources.displayMetrics.widthPixels * p.getInt(SettingsActivity.KEY_WIDTH, 100) / 100f).roundToInt()
        root.addView(box, LinearLayout.LayoutParams(width, -2))

        val bar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        key(bar, "☰", .8f, fg) { tools = !tools; refresh() }
        key(bar, "AI", .8f, fg) { ai() }
        key(bar, "⌕", .8f, fg) { search() }
        key(bar, "📋", .8f, fg) { paste() }
        key(bar, "🌐", .8f, fg) { language() }
        val currentLanguage = p.getString(SettingsActivity.KEY_LANGUAGE, "English (US)") ?: "English (US)"
        bar.addView(
            TextView(this).apply {
                text = currentLanguage.take(3).uppercase()
                setTextColor(fg)
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(0, scaled(42), 1f)
        )
        key(bar, "⚙", .8f, fg) {
            startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        box.addView(bar, LinearLayout.LayoutParams(-1, scaled(45)))

        if (tools) toolRow(box, fg) else suggestions(box, fg)

        val layout = p.getString(SettingsActivity.KEY_LAYOUT, "QWERTY") ?: "QWERTY"
        val rows = if (symbols) {
            listOf("1234567890", "@#$%&*+-=", "()[]{}!?/")
        } else {
            KeyboardLayouts.rows(currentLanguage, layout)
        }

        rows.forEachIndexed { index, row ->
            val keyRow = LinearLayout(this).apply { gravity = Gravity.CENTER }
            if (index == 2) {
                key(keyRow, if (symbols) "ABC" else "⇧", 1f, fg) {
                    if (symbols) symbols = false else shift = !shift
                    refresh()
                }
            }
            row.forEach { ch ->
                val value = if (shift && !symbols) ch.uppercaseChar().toString() else ch.toString()
                key(keyRow, value, 1f, fg) {
                    commit(value)
                    if (shift && !symbols) {
                        shift = false
                        refresh()
                    }
                }
            }
            if (index == 2) key(keyRow, "⌫", 1.2f, fg) { backspace() }
            box.addView(keyRow, LinearLayout.LayoutParams(-1, scaled(if (compact) 43 else 51)))
        }

        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER }
        key(bottom, "#+=", .9f, fg) { symbols = !symbols; refresh() }
        key(bottom, ",", .8f, fg) { commit(",") }
        key(bottom, "space", 4f, fg) { space() }
        key(bottom, ".", .8f, fg) { commit(".") }
        key(bottom, "↵", 1.1f, fg) { enter() }
        box.addView(bottom, LinearLayout.LayoutParams(-1, scaled(56)))
        return root
    }

    private fun suggestions(box: LinearLayout, fg: Int) {
        val text = currentInputConnection?.getTextBeforeCursor(80, 0)?.toString().orEmpty()
        val prefix = text.takeLastWhile { it.isLetter() }
        val previous = text.dropLast(prefix.length).trimEnd().substringAfterLast(' ')
        val words = (model.suggestions(prefix, previous) + FeatureEngine.predictions(previous))
            .distinct().take(3).ifEmpty { listOf("the", "and", "you") }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        words.forEach { word -> key(row, word, 1f, fg) { commit(word + " ") } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(44)))
    }

    private fun toolRow(box: LinearLayout, fg: Int) {
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("😊 Emoji", "Translate", "GIFs", "Stickers", "Layout").forEach { item ->
            key(row, item, 1f, fg) {
                when (item.substringBefore(' ')) {
                    "😊" -> commit("😀")
                    "Translate" -> commit("[Translate] ")
                    "GIFs", "Stickers" -> Toast.makeText(this, "Configure provider in Settings", Toast.LENGTH_SHORT).show()
                    "Layout" -> startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun key(row: LinearLayout, text: String, weight: Float, fg: Int, action: () -> Unit) {
        val button = Button(this).apply {
            this.text = text
            textSize = if (text.length > 1) 10f else 18f
            setTextColor(fg)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            minHeight = 0
            minWidth = 0
            background = GradientDrawable().apply {
                setColor(if (dark) Color.rgb(31, 41, 55) else Color.WHITE)
                cornerRadius = dp(9).toFloat()
            }
            setOnClickListener { action() }
        }
        row.addView(
            button,
            LinearLayout.LayoutParams(0, scaled(if (text.length > 1) 42 else if (compact) 43 else 51), weight).apply {
                setMargins(dp(2), dp(2), dp(2), dp(2))
            }
        )
    }

    private fun commit(text: String) {
        currentInputConnection?.commitText(text, 1)
        if (!p.getBoolean(SettingsActivity.KEY_INCOGNITO, false)) model.learn(text)
    }

    private fun paste() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.primaryClip?.let { clip ->
            if (clip.itemCount > 0) commit(clip.getItemAt(0).coerceToText(this).toString())
        }
    }

    private fun space() {
        if (currentInputConnection?.getTextBeforeCursor(2, 0)?.toString() == "  ") {
            currentInputConnection?.deleteSurroundingText(2, 0)
            commit(". ")
        } else commit(" ")
    }

    private fun backspace() {
        val connection = currentInputConnection ?: return
        val before = connection.getTextBeforeCursor(80, 0)?.toString().orEmpty()
        val count = if (before.endsWith(" ")) 1 else before.takeLastWhile { !it.isWhitespace() }.length.coerceAtLeast(1)
        connection.deleteSurroundingText(count, 0)
    }

    private fun enter() {
        val connection = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: 0
        if (action != 0) connection.performEditorAction(action)
        else {
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            connection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun ai() {
        val text = currentInputConnection?.getTextBeforeCursor(500, 0)?.toString().orEmpty()
        if (text.isBlank()) Toast.makeText(this, "Type text first", Toast.LENGTH_SHORT).show()
        else commit(FeatureEngine.tonePrompt("professional", text))
    }

    private fun search() {
        val query = currentInputConnection?.getTextBeforeCursor(150, 0)?.toString().orEmpty()
        startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(query))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun language() {
        val languages = KeyboardLayouts.languages
        val old = p.getString(SettingsActivity.KEY_LANGUAGE, languages[0]) ?: languages[0]
        p.edit().putString(SettingsActivity.KEY_LANGUAGE, languages[(languages.indexOf(old) + 1).mod(languages.size)]).apply()
        refresh()
    }

    private fun refresh() { setInputView(build()) }
    private fun scaled(value: Int) = dp(value * scale * height)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
}
