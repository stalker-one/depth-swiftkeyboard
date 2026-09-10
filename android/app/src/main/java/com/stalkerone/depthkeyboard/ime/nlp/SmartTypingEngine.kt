package com.stalkerone.depthkeyboard.ime.nlp

import java.util.Locale

class SmartTypingEngine {
    private val corrections = mapOf(
        "teh" to "the", "adn" to "and", "recieve" to "receive", "seperate" to "separate",
        "definately" to "definitely", "occured" to "occurred", "adress" to "address",
        "becuase" to "because", "tommorow" to "tomorrow", "writting" to "writing",
        "dont" to "don't", "cant" to "can't", "wont" to "won't", "im" to "I'm", "ive" to "I've"
    )

    fun correct(word: String): String {
        val replacement = corrections[word.lowercase(Locale.ROOT)] ?: return word
        return if (word.firstOrNull()?.isUpperCase() == true) replacement.replaceFirstChar { it.uppercase() } else replacement
    }

    fun correctSentence(text: String): String = text.split(Regex("(?<=\\s)|(?=[,.!?])")).joinToString("") { token ->
        if (token.any { it.isLetter() } && token.all { it.isLetter() || it == '\'' }) correct(token) else token
    }

    fun punctuationSpacing(text: String): String = text.replace(Regex("\\s+([,.!?])"), "$1").replace(Regex("([,.!?])(?=[A-Za-z])"), "$1 ")

    fun capitalize(text: String): String {
        if (text.isBlank()) return text
        val out = StringBuilder(text)
        var sentenceStart = true
        out.forEachIndexed { i, ch ->
            if (sentenceStart && ch.isLetter()) { out.setCharAt(i, ch.uppercaseChar()); sentenceStart = false }
            if (ch in ".!?" ) sentenceStart = true
        }
        return out.toString()
    }
}
