package com.stalkerone.depthkeyboard.ime.input

object LayoutProfiles {
    val names = listOf("QWERTY", "QWERTZ", "AZERTY", "DVORAK", "COLEMAK")
    val modes = listOf("standard", "one-handed", "floating", "split", "tablet", "compact")
    val symbols = listOf("1234567890", "@#\$%&*+-=", "()[]{}!?/\\")
    fun normalize(name: String) = if (names.contains(name)) name else "QWERTY"
    fun isValidMode(mode: String) = modes.contains(mode)
}
