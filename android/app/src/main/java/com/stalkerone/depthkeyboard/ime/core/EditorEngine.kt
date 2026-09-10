package com.stalkerone.depthkeyboard.ime.core

import android.view.KeyEvent
import android.view.inputmethod.InputConnection

object EditorEngine {
    fun textBefore(ic: InputConnection?, max: Int = 500): String = ic?.getTextBeforeCursor(max, 0)?.toString().orEmpty()
    fun textAfter(ic: InputConnection?, max: Int = 500): String = ic?.getTextAfterCursor(max, 0)?.toString().orEmpty()
    fun commit(ic: InputConnection?, text: String) { ic?.commitText(text, 1) }
    fun backspace(ic: InputConnection?) {
        val before = textBefore(ic, 80)
        val count = if (before.endsWith(" ")) 1 else before.takeLastWhile { !it.isWhitespace() }.length.coerceAtLeast(1)
        ic?.deleteSurroundingText(count, 0)
    }
    fun deleteWord(ic: InputConnection?) {
        val before = textBefore(ic, 80)
        val count = before.takeLastWhile { !it.isWhitespace() }.length.coerceAtLeast(1)
        ic?.deleteSurroundingText(count, 0)
    }
    fun sendEnter(ic: InputConnection?) {
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }
}
