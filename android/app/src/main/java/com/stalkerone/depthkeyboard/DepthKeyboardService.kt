package com.stalkerone.depthkeyboard

import android.content.Intent
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
    private var capsLock = false
    private var symbols = false
    private var tools = false
    private var clipboardMode = false
    private lateinit var model: TypingModel

    private val p get() = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE)
    private val scale get() = p.getInt(SettingsActivity.KEY_SIZE, 100) / 100f
    private val height get() = p.getInt(SettingsActivity.KEY_HEIGHT, 100) / 100f
    private val compact get() = p.getBoolean(SettingsActivity.KEY_COMPACT, false)

    override fun onCreate() { super.onCreate(); model = TypingModel(this) }
    override fun onCreateInputView(): View = build()
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) { super.onStartInput(attribute, restarting); shift = false; capsLock = false; symbols = false; tools = false; clipboardMode = false }
    override fun onEvaluateInputViewShown() = true

    private fun build(): View {
        val theme = ThemeCatalog.current(this)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(5), dp(3), dp(5), dp(7)); setBackgroundColor(theme.background) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val mode = p.getString(SettingsActivity.KEY_LAYOUT_MODE, "standard")
        val modeScale = when (mode) { "one-handed" -> .78f; "floating" -> .84f else -> 1f }
        val width = (resources.displayMetrics.widthPixels * p.getInt(SettingsActivity.KEY_WIDTH, 100) / 100f * modeScale).roundToInt()
        root.addView(box, LinearLayout.LayoutParams(width, -2))
        val bar = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        key(bar, "☰", .8f, theme.text) { tools = !tools; clipboardMode = false; refresh() }
        key(bar, "AI", .8f, theme.text) { ai() }
        key(bar, "⌕", .8f, theme.text) { search() }
        key(bar, "📋", .8f, theme.text) { tools = true; clipboardMode = true; refresh() }
        key(bar, "🌐", .8f, theme.text) { language() }
        val currentLanguage = p.getString(SettingsActivity.KEY_LANGUAGE, "English (US)") ?: "English (US)"
        bar.addView(TextView(this).apply { text = currentLanguage.take(3).uppercase(); setTextColor(theme.text); gravity = Gravity.CENTER }, LinearLayout.LayoutParams(0, scaled(42), 1f))
        key(bar, "⚙", .8f, theme.text) { startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        box.addView(bar, LinearLayout.LayoutParams(-1, scaled(45)))

        if (tools) { toolRow(box, theme.text); if (clipboardMode) clipboardRow(box, theme.text) else emojiRow(box, theme.text) } else suggestions(box, theme.text)

        val layout = p.getString(SettingsActivity.KEY_LAYOUT, "QWERTY") ?: "QWERTY"
        val baseRows = if (symbols) listOf("1234567890", "@#$%&*+-=", "()[]{}!?/") else KeyboardLayouts.rows(currentLanguage, layout)
        val rows = if (!symbols && p.getBoolean(SettingsActivity.KEY_NUMBER_ROW, false)) listOf("1234567890") + baseRows else baseRows
        rows.forEachIndexed { index, row ->
            val keyRow = LinearLayout(this).apply { gravity = Gravity.CENTER; if (p.getBoolean(SettingsActivity.KEY_SPLIT, false) && !symbols) setPadding(dp(10), 0, dp(10), 0) }
            if (index == rows.lastIndex) key(keyRow, if (symbols) "ABC" else if (capsLock) "⇪" else "⇧", 1f, theme.text) { if (symbols) symbols = false else if (shift) { capsLock = true; shift = false } else if (capsLock) { capsLock = false } else shift = true; refresh() }
            row.forEach { ch ->
                val value = if ((shift || capsLock) && !symbols) ch.uppercaseChar().toString() else ch.toString()
                key(keyRow, value, 1f, theme.text, { anchor -> showAccents(anchor, ch) }) { commit(value); if (shift && !capsLock && !symbols) { shift = false; refresh() } }
            }
            if (index == rows.lastIndex) key(keyRow, "⌫", 1.2f, theme.text) { backspace() }
            box.addView(keyRow, LinearLayout.LayoutParams(-1, scaled(if (compact) 43 else 51)))
        }
        val bottom = LinearLayout(this).apply { gravity = Gravity.CENTER }
        key(bottom, "#+=", .9f, theme.text) { symbols = !symbols; refresh() }
        key(bottom, ",", .8f, theme.text) { commit(",") }
        key(bottom, "space", 4f, theme.text) { space() }
        key(bottom, ".", .8f, theme.text) { commit(".") }
        key(bottom, "↵", 1.1f, theme.text) { enter() }
        box.addView(bottom, LinearLayout.LayoutParams(-1, scaled(56)))
        return root
    }

    private fun suggestions(box: LinearLayout, fg: Int) {
        val text = currentInputConnection?.getTextBeforeCursor(80, 0)?.toString().orEmpty()
        val prefix = text.takeLastWhile { it.isLetter() }
        val previous = text.dropLast(prefix.length).trimEnd().substringAfterLast(' ')
        val words = (model.suggestions(prefix, previous) + FeatureEngine.predictions(previous)).distinct().take(3).ifEmpty { listOf("the", "and", "you") }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        words.forEach { word -> key(row, word, 1f, fg) { commit(word + " ") } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(44)))
    }

    private fun toolRow(box: LinearLayout, fg: Int) {
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("😊 Emoji", "📋 Clips", "Translate", "GIFs", "Layout").forEach { item ->
            key(row, item, 1f, fg) {
                when (item.substringBefore(' ')) {
                    "😊" -> { clipboardMode = false; refresh() }
                    "📋" -> { clipboardMode = true; refresh() }
                    "Translate" -> commit("[Translate] ")
                    "GIFs" -> Toast.makeText(this, "GIF provider can be configured in Settings", Toast.LENGTH_SHORT).show()
                    "Layout" -> startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun emojiRow(box: LinearLayout, fg: Int) {
        val categories = EmojiCatalog.categories.keys.toList()
        val category = p.getString(SettingsActivity.KEY_EMOJI_CATEGORY, "Recent") ?: "Recent"
        val tabs = LinearLayout(this).apply { gravity = Gravity.CENTER }
        categories.take(4).forEach { name -> key(tabs, name, 1f, fg) { p.edit().putString(SettingsActivity.KEY_EMOJI_CATEGORY, name).apply(); refresh() } }
        box.addView(tabs, LinearLayout.LayoutParams(-1, scaled(38)))
        val recent = if (category == "Recent") p.getString("recent_emojis", "")!!.split('|').filter { it.isNotBlank() }.ifEmpty { EmojiCatalog.categories["Recent"].orEmpty() } else EmojiCatalog.categories[category].orEmpty()
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        recent.take(8).forEach { emoji -> key(row, emoji, 1f, fg) { rememberEmoji(emoji); commit(emoji) } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun rememberEmoji(emoji: String) { val recent = p.getString("recent_emojis", "")!!.split('|').filter { it.isNotBlank() }; p.edit().putString("recent_emojis", (listOf(emoji) + recent.filterNot { it == emoji }).take(20).joinToString("|")).apply() }

    private fun clipboardRow(box: LinearLayout, fg: Int) {
        val clips = ClipboardStore(this).items().take(3)
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        if (clips.isEmpty()) key(row, "No saved clips", 1f, fg) { clipboardMode = false; refresh() }
        else clips.forEach { clip -> key(row, clip.take(18), 1f, fg) { commit(clip); clipboardMode = false; refresh() } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun key(row: LinearLayout, text: String, weight: Float, fg: Int, longAction: ((View) -> Unit)? = null, action: () -> Unit) {
        val theme = ThemeCatalog.current(this)
        val button = Button(this).apply { this.text = text; textSize = if (text.length > 1) 10f else 18f; setTextColor(fg); isAllCaps = false; setPadding(0, 0, 0, 0); minHeight = 0; minWidth = 0; background = GradientDrawable().apply { setColor(theme.key); cornerRadius = dp(9).toFloat() }; setOnClickListener { action() }; setOnLongClickListener { longAction?.invoke(this); longAction != null } }
        row.addView(button, LinearLayout.LayoutParams(0, scaled(if (text.length > 1) 42 else if (compact) 43 else 51), weight).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) })
    }

    private fun commit(text: String) { currentInputConnection?.commitText(text, 1); if (!p.getBoolean(SettingsActivity.KEY_INCOGNITO, false)) { ClipboardStore(this).add(text); model.learn(text) } }
    private fun paste() { ClipboardStore(this).items().firstOrNull()?.let { commit(it) } }
    private fun space() { if (p.getBoolean(SettingsActivity.KEY_DOUBLE_SPACE, true) && currentInputConnection?.getTextBeforeCursor(2, 0)?.toString() == "  ") { currentInputConnection?.deleteSurroundingText(2, 0); commit(". ") } else if (p.getBoolean(SettingsActivity.KEY_AUTOCORRECT, true)) { autocorrectAndSpace() } else commit(" ") }
    private fun autocorrectAndSpace() { val c = currentInputConnection ?: return; val before = c.getTextBeforeCursor(80, 0)?.toString().orEmpty(); val word = before.takeLastWhile { !it.isWhitespace() }; val corrected = FeatureEngine.autocorrect(word); if (corrected != word && word.isNotEmpty()) { c.deleteSurroundingText(word.length, 0); commit(corrected) }; commit(" ") }
    private fun backspace() { val c = currentInputConnection ?: return; val before = c.getTextBeforeCursor(80, 0)?.toString().orEmpty(); val count = if (before.endsWith(" ")) 1 else before.takeLastWhile { !it.isWhitespace() }.length.coerceAtLeast(1); c.deleteSurroundingText(count, 0) }
    private fun enter() { val c = currentInputConnection ?: return; val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: 0; if (action != 0) c.performEditorAction(action) else { c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)); c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)) } }
    private fun ai() { val text = currentInputConnection?.getTextBeforeCursor(500, 0)?.toString().orEmpty(); if (text.isBlank()) Toast.makeText(this, "Type text first", Toast.LENGTH_SHORT).show() else commit(FeatureEngine.tonePrompt("professional", text)) }
    private fun search() { val query = currentInputConnection?.getTextBeforeCursor(150, 0)?.toString().orEmpty(); startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(query))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    private fun language() { val languages = KeyboardLayouts.languages; val old = p.getString(SettingsActivity.KEY_LANGUAGE, languages[0]) ?: languages[0]; p.edit().putString(SettingsActivity.KEY_LANGUAGE, languages[(languages.indexOf(old) + 1).mod(languages.size)]).apply(); refresh() }
    private fun showAccents(anchor: View, base: Char) { if (!p.getBoolean(SettingsActivity.KEY_LONG_PRESS, true)) return; val accents = KeyboardLayouts.accents(base); if (accents.isEmpty()) { Toast.makeText(this, "No alternate characters", Toast.LENGTH_SHORT).show(); return }; commit(accents.first()) }
    private fun refresh() { setInputView(build()) }
    private fun scaled(value: Int) = dp(value * scale * height)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
    private fun dp(value: Float) = (value * resources.displayMetrics.density).roundToInt()
}
