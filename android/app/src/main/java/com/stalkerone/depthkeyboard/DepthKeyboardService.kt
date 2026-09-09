package com.stalkerone.depthkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View

class DepthKeyboardService : InputMethodService() {
    override fun onCreateInputView(): View = android.widget.TextView(this).apply { text = "Depth Keyboard" }
}
