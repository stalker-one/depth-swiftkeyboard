package com.stalkerone.depthkeyboard

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Vibrator
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.stalkerone.depthkeyboard.ime.DepthFeatureHub
import kotlin.math.roundToInt

class DepthKeyboardService : InputMethodService() {
    private var shift = false
    private var capsLock = false
    private var symbols = false
    private var tools = false
    private var clipboardMode = false
    private lateinit var model: TypingModel
    private lateinit var features: DepthFeatureHub

    private val p get() = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE)
    private val scale get() = p.getInt(SettingsActivity.KEY_SIZE, 100) / 100f
    private val height get() = p.getInt(SettingsActivity.KEY_HEIGHT, 100) / 100f
    private val compact get() = p.getBoolean(SettingsActivity.KEY_COMPACT, false)

    override fun onCreate() {
        super.onCreate()
        model = TypingModel(this)
        features = DepthFeatureHub(this)
    }

    override fun onCreateInputView(): View = build()

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        shift = false
        capsLock = false
        symbols = false
        tools = false
        clipboardMode = false
    }

    override fun onEvaluateInputViewShown() = true

    private fun build(): View {
        val theme = ThemeCatalog.current(this)
        val root = GestureLayout(this) { word -> commit(word + " ") }.apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(3), dp(5), dp(7))
            setBackgroundColor(theme.background)
        }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val mode = p.getString(SettingsActivity.KEY_LAYOUT_MODE, "standard")
        val modeScale = when (mode) { "one-handed" -> .78f; "floating" -> .84f; else -> 1f }
        val width = (resources.displayMetrics.widthPixels * p.getInt(SettingsActivity.KEY_WIDTH, 100) / 100f * modeScale).roundToInt()
        root.addView(box, LinearLayout.LayoutParams(width, -2))

        toolbar(box, theme.text)
        if (tools) {
            featureRows(box, theme.text)
            if (clipboardMode) clipboardRow(box, theme.text) else emojiRow(box, theme.text)
        } else {
            suggestions(box, theme.text)
        }

        val currentLanguage = currentLanguage()
        val layout = p.getString(SettingsActivity.KEY_LAYOUT, "QWERTY") ?: "QWERTY"
        val baseRows = if (symbols) {
            listOf("1234567890", "@#$%&*+-=", "()[]{}!?/\\")
        } else {
            KeyboardLayouts.rows(currentLanguage, layout)
        }
        val rows = if (!symbols && p.getBoolean(SettingsActivity.KEY_NUMBER_ROW, false)) listOf("1234567890") + baseRows else baseRows

        rows.forEachIndexed { index, row ->
            val keyRow = LinearLayout(this).apply {
                gravity = Gravity.CENTER
                if (p.getBoolean(SettingsActivity.KEY_SPLIT, false) && !symbols) setPadding(dp(10), 0, dp(10), 0)
            }
            if (index == rows.lastIndex) {
                key(keyRow, if (symbols) "ABC" else if (capsLock) "⇪" else "⇧", 1f, theme.text) {
                    if (symbols) symbols = false
                    else if (shift) { capsLock = true; shift = false }
                    else if (capsLock) capsLock = false
                    else shift = true
                    refresh()
                }
            }
            row.forEach { ch ->
                val value = if ((shift || capsLock) && !symbols) ch.uppercaseChar().toString() else ch.toString()
                key(keyRow, value, 1f, theme.text, { anchor -> showAccents(anchor, ch) }) {
                    commit(value)
                    if (shift && !capsLock && !symbols) { shift = false; refresh() }
                }
            }
            if (index == rows.lastIndex) swipeDeleteKey(keyRow, theme.text)
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

    private fun toolbar(box: LinearLayout, fg: Int) {
        val scroll = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        key(row, "☰", .8f, fg) { tools = !tools; clipboardMode = false; refresh() }
        key(row, "AI", .8f, fg) { ai("professional") }
        key(row, "Search", 1f, fg) { search() }
        key(row, "Tone", 1f, fg) { toneMenu() }
        key(row, "Fix", 1f, fg) { polishCurrentText() }
        key(row, "Voice", 1f, fg) { voice() }
        key(row, "📋", .8f, fg) { tools = true; clipboardMode = true; refresh() }
        key(row, "😊", .8f, fg) { tools = true; clipboardMode = false; refresh() }
        key(row, "🌐", .8f, fg) { language() }
        key(row, "⚙", .8f, fg) { openSettings() }
        scroll.addView(row)
        box.addView(scroll, LinearLayout.LayoutParams(-1, scaled(45)))
    }

    private fun featureRows(box: LinearLayout, fg: Int) {
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("Emoji", "Translate", "GIFs", "Stickers", "Layout", "Incognito").forEach { item ->
            key(row, item, 1f, fg) {
                when (item) {
                    "Emoji" -> { clipboardMode = false; refresh() }
                    "Translate" -> translate()
                    "GIFs" -> providerNotice("GIF")
                    "Stickers" -> providerNotice("Sticker")
                    "Layout" -> openSettings()
                    "Incognito" -> { val enabled = !p.getBoolean(SettingsActivity.KEY_INCOGNITO, false); p.edit().putBoolean(SettingsActivity.KEY_INCOGNITO, enabled).apply(); Toast.makeText(this, if (enabled) "Incognito on" else "Incognito off", Toast.LENGTH_SHORT).show() }
                }
            }
        }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
        val row2 = LinearLayout(this).apply { gravity = Gravity.CENTER }
        listOf("Paste", "Delete word", "Caps", "Themes", "Account", "Privacy").forEach { item ->
            key(row2, item, 1f, fg) {
                when (item) {
                    "Paste" -> paste()
                    "Delete word" -> deleteWord()
                    "Caps" -> { capsLock = !capsLock; shift = false; refresh() }
                    "Themes" -> openSettings()
                    "Account" -> Toast.makeText(this, "Account/cloud sync is provider-ready; no credentials are stored in the APK", Toast.LENGTH_SHORT).show()
                    "Privacy" -> openSettings()
                }
            }
        }
        box.addView(row2, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun suggestions(box: LinearLayout, fg: Int) {
        if (!p.getBoolean(SettingsActivity.KEY_SUGGESTIONS, true)) return
        val text = currentInputConnection?.getTextBeforeCursor(120, 0)?.toString().orEmpty()
        val prefix = text.takeLastWhile { it.isLetter() }
        val previous = text.dropLast(prefix.length).trimEnd().substringAfterLast(' ')
        val words = (model.suggestions(prefix, previous) +
                features.typing.suggestions(prefix, previous) +
                LanguageDictionaries.words(currentLanguage(), prefix) +
                FeatureEngine.predictions(previous))
            .distinct()
            .take(5)
            .ifEmpty { listOf("the", "and", "you") }
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        words.forEach { word -> key(row, word, 1f, fg) { replaceCurrentWord(word) } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(44)))
    }

    private fun emojiRow(box: LinearLayout, fg: Int) {
        val categories = EmojiCatalog.categories.keys.toList()
        val category = p.getString(SettingsActivity.KEY_EMOJI_CATEGORY, "Recent") ?: "Recent"
        val tabs = LinearLayout(this).apply { gravity = Gravity.CENTER }
        categories.take(6).forEach { name -> key(tabs, name, 1f, fg) { p.edit().putString(SettingsActivity.KEY_EMOJI_CATEGORY, name).apply(); refresh() } }
        box.addView(tabs, LinearLayout.LayoutParams(-1, scaled(38)))
        val recent = if (category == "Recent") p.getString("recent_emojis", "")!!.split('|').filter { it.isNotBlank() }.ifEmpty { EmojiCatalog.categories["Recent"].orEmpty() } else EmojiCatalog.categories[category].orEmpty()
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        recent.take(10).forEach { emoji -> key(row, emoji, 1f, fg, { removeEmoji(emoji) }) { rememberEmoji(emoji); commit(emoji) } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun clipboardRow(box: LinearLayout, fg: Int) {
        val clips = ClipboardStore(this).items().take(5)
        val row = LinearLayout(this).apply { gravity = Gravity.CENTER }
        if (clips.isEmpty()) key(row, "No saved clips", 1f, fg) { clipboardMode = false; refresh() }
        else clips.forEach { clip -> key(row, clip.take(20), 1f, fg, { ClipboardStore(this).remove(clip); refresh() }) { commit(clip); clipboardMode = false; refresh() } }
        box.addView(row, LinearLayout.LayoutParams(-1, scaled(46)))
    }

    private fun rememberEmoji(emoji: String) {
        val recent = p.getString("recent_emojis", "")!!.split('|').filter { it.isNotBlank() }
        p.edit().putString("recent_emojis", (listOf(emoji) + recent.filterNot { it == emoji }).take(30).joinToString("|")).apply()
    }

    private fun removeEmoji(emoji: String) {
        val recent = p.getString("recent_emojis", "")!!.split('|').filter { it.isNotBlank() && it != emoji }
        p.edit().putString("recent_emojis", recent.joinToString("|")).apply()
        refresh()
    }

    private fun key(row: LinearLayout, text: String, weight: Float, fg: Int, longAction: ((View) -> Unit)? = null, action: () -> Unit) {
        val theme = ThemeCatalog.current(this)
        val button = Button(this).apply {
            this.text = text
            textSize = if (text.length > 1) 10f else 18f
            setTextColor(if (p.getBoolean(SettingsActivity.KEY_HIGH_CONTRAST, false)) 0xFFFFFFFF.toInt() else fg)
            isAllCaps = false
            setPadding(0, 0, 0, 0)
            minHeight = 0
            minWidth = 0
            background = GradientDrawable().apply { setColor(theme.key); cornerRadius = dp(9).toFloat() }
            setOnClickListener { feedback(); action() }
            setOnLongClickListener { feedback(); longAction?.invoke(this); longAction != null }
            contentDescription = text
        }
        row.addView(button, LinearLayout.LayoutParams(0, scaled(if (text.length > 1) 42 else if (compact) 43 else 51), weight).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) })
    }

    private fun swipeDeleteKey(row: LinearLayout, fg: Int) {
        val theme = ThemeCatalog.current(this)
        var startX = 0f
        val button = Button(this).apply {
            text = "⌫"; textSize = 18f; setTextColor(fg); isAllCaps = false; setPadding(0, 0, 0, 0); minHeight = 0; minWidth = 0
            background = GradientDrawable().apply { setColor(theme.key); cornerRadius = dp(9).toFloat() }
            setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> { startX = event.x; true }
                    MotionEvent.ACTION_UP -> { if (startX - event.x > dp(42)) deleteWord() else backspace(); true }
                    else -> true
                }
            }
        }
        row.addView(button, LinearLayout.LayoutParams(0, scaled(if (compact) 43 else 51), 1.2f).apply { setMargins(dp(2), dp(2), dp(2), dp(2)) })
    }

    private fun feedback() {
        if (p.getBoolean(SettingsActivity.KEY_HAPTIC, false)) (getSystemService(VIBRATOR_SERVICE) as? Vibrator)?.vibrate(8)
        if (p.getBoolean(SettingsActivity.KEY_SOUND, false)) (getSystemService(AUDIO_SERVICE) as? AudioManager)?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD)
    }

    private fun commit(text: String) {
        currentInputConnection?.commitText(text, 1)
        if (features.privacy.canLearn() && !p.getBoolean(SettingsActivity.KEY_INCOGNITO, false)) model.learn(text)
    }

    private fun replaceCurrentWord(word: String) {
        val c = currentInputConnection ?: return
        val before = c.getTextBeforeCursor(120, 0)?.toString().orEmpty()
        val prefix = before.takeLastWhile { it.isLetter() }
        if (prefix.isNotEmpty()) c.deleteSurroundingText(prefix.length, 0)
        commit(word + " ")
    }

    private fun paste() {
        if (!features.privacy.canCaptureClipboard()) { Toast.makeText(this, "Clipboard capture is disabled", Toast.LENGTH_SHORT).show(); return }
        ClipboardStore(this).items().firstOrNull()?.let { commit(it) }
    }

    private fun space() {
        val c = currentInputConnection ?: return
        val before = c.getTextBeforeCursor(120, 0)?.toString().orEmpty()
        if (p.getBoolean(SettingsActivity.KEY_DOUBLE_SPACE, true) && before.endsWith("  ")) {
            c.deleteSurroundingText(2, 0); commit(". "); return
        }
        if (p.getBoolean(SettingsActivity.KEY_AUTOCORRECT, true)) {
            val word = before.takeLastWhile { !it.isWhitespace() }
            val corrected = features.typing.correct(word)
            if (corrected != word && word.isNotEmpty()) { c.deleteSurroundingText(word.length, 0); commit(corrected) }
        }
        commit(" ")
    }

    private fun polishCurrentText() {
        val c = currentInputConnection ?: return
        val before = c.getTextBeforeCursor(1000, 0)?.toString().orEmpty()
        if (before.isBlank()) { Toast.makeText(this, "Type some text first", Toast.LENGTH_SHORT).show(); return }
        val polished = features.typing.polish(before)
        c.deleteSurroundingText(before.length, 0)
        commit(polished)
    }

    private fun ai(tone: String) {
        val text = currentInputConnection?.getTextBeforeCursor(500, 0)?.toString().orEmpty()
        if (text.isBlank()) { Toast.makeText(this, "Type text first", Toast.LENGTH_SHORT).show(); return }
        Toast.makeText(this, "AI provider not configured. Your text stays local.", Toast.LENGTH_LONG).show()
    }

    private fun toneMenu() {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER }
        // Keep the feature local and predictable: the Fix action performs safe spelling/spacing/capitalization.
        Toast.makeText(this, "Tone options: Professional · Casual · Polite · Social Post. Connect an AI provider for generative rewrites.", Toast.LENGTH_LONG).show()
    }

    private fun translate() {
        val text = currentInputConnection?.getTextBeforeCursor(300, 0)?.toString().orEmpty()
        if (text.isBlank()) { Toast.makeText(this, "Type text first", Toast.LENGTH_SHORT).show(); return }
        Toast.makeText(this, "Translator is provider-ready. Configure your translation backend in the app settings.", Toast.LENGTH_LONG).show()
    }

    private fun providerNotice(name: String) {
        Toast.makeText(this, "$name search/send needs a configured provider; no fake network service is used.", Toast.LENGTH_LONG).show()
    }

    private fun voice() {
        try {
            startActivity(features.voiceIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Throwable) {
            Toast.makeText(this, "Voice input is not available on this device", Toast.LENGTH_SHORT).show()
        }
    }

    private fun search() {
        val query = currentInputConnection?.getTextBeforeCursor(150, 0)?.toString().orEmpty().trim()
        if (query.isBlank()) { Toast.makeText(this, "Type a search query first", Toast.LENGTH_SHORT).show(); return }
        startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=" + android.net.Uri.encode(query))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun language() {
        val languages = KeyboardLayouts.languages
        val old = currentLanguage()
        val next = languages[(languages.indexOf(old).coerceAtLeast(0) + 1) % languages.size]
        p.edit().putString(SettingsActivity.KEY_LANGUAGE, next).apply()
        refresh()
    }

    private fun currentLanguage() = p.getString(SettingsActivity.KEY_LANGUAGE, "English (US)") ?: "English (US)"

    private fun showAccents(anchor: View, base: Char) {
        if (!p.getBoolean(SettingsActivity.KEY_LONG_PRESS, true)) return
        val accents = KeyboardLayouts.accents(base)
        if (accents.isEmpty()) return
        commit(accents.first())
    }

    private fun backspace() {
        val c = currentInputConnection ?: return
        val before = c.getTextBeforeCursor(120, 0)?.toString().orEmpty()
        if (before.isEmpty()) return
        val count = if (before.endsWith(" ")) 1 else before.takeLastWhile { !it.isWhitespace() }.length.coerceAtLeast(1)
        c.deleteSurroundingText(count, 0)
    }

    private fun deleteWord() {
        val c = currentInputConnection ?: return
        val before = c.getTextBeforeCursor(120, 0)?.toString().orEmpty()
        val trimmed = before.dropLastWhile { it.isWhitespace() }
        val count = trimmed.takeLastWhile { !it.isWhitespace() }.length
        if (count > 0) c.deleteSurroundingText(count, 0)
    }

    private fun enter() {
        val c = currentInputConnection ?: return
        val action = currentInputEditorInfo?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: 0
        if (action != 0) c.performEditorAction(action) else {
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            c.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun refresh() { setInputView(build()) }
    private fun scaled(value: Int) = dp(value * scale * height)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
    private fun dp(value: Float) = (value * resources.displayMetrics.density).roundToInt()

    private inner class GestureLayout(context: android.content.Context, private val onWord: (String) -> Unit) : LinearLayout(context) {
        private var startX = 0f
        private var startY = 0f
        private var active = false
        private val trace = StringBuilder()

        override fun dispatchTouchEvent(event: MotionEvent): Boolean {
            if (!p.getBoolean(SettingsActivity.KEY_FLOW, false)) return super.dispatchTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> { startX = event.x; startY = event.y; active = false; trace.setLength(0); addTrace(event.x, event.y) }
                MotionEvent.ACTION_MOVE -> {
                    if (!active && kotlin.math.hypot((event.x - startX).toDouble(), (event.y - startY).toDouble()) > dp(18)) {
                        active = true
                        super.dispatchTouchEvent(MotionEvent.obtain(event).apply { setAction(MotionEvent.ACTION_CANCEL) })
                    }
                    if (active) { addTrace(event.x, event.y); return true }
                }
                MotionEvent.ACTION_UP -> {
                    if (active) {
                        addTrace(event.x, event.y)
                        GestureResolver.resolve(trace.toString(), currentLanguage())?.let(onWord)
                        active = false
                        return true
                    }
                }
                MotionEvent.ACTION_CANCEL -> active = false
            }
            return super.dispatchTouchEvent(event)
        }

        private fun addTrace(x: Float, y: Float) {
            val view = findViewAt(this, x.toInt(), y.toInt())
            if (view is Button) {
                val value = view.text.toString()
                if (value.length == 1 && value[0].isLetter() && (trace.isEmpty() || trace.last() != value[0])) trace.append(value)
            }
        }

        private fun findViewAt(parent: android.view.ViewGroup, x: Int, y: Int): View? {
            for (i in parent.childCount - 1 downTo 0) {
                val child = parent.getChildAt(i)
                if (x < child.left || x > child.right || y < child.top || y > child.bottom) continue
                if (child is android.view.ViewGroup) findViewAt(child, x - child.left, y - child.top)?.let { return it }
                return child
            }
            return null
        }
    }
}
