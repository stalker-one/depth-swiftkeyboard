package com.stalkerone.depthkeyboard.ime.nlp

import java.util.Locale

/** Local-first typing intelligence. No network access and no secrets. */
class SmartTypingEngine {
    private val corrections = mapOf(
        "teh" to "the", "adn" to "and", "recieve" to "receive", "seperate" to "separate",
        "definately" to "definitely", "occured" to "occurred", "adress" to "address",
        "becuase" to "because", "tommorow" to "tomorrow", "writting" to "writing",
        "untill" to "until", "wich" to "which", "whcih" to "which", "thier" to "their",
        "wierd" to "weird", "alot" to "a lot", "reciept" to "receipt", "begining" to "beginning",
        "enviroment" to "environment", " goverment" to " government", "doesnt" to "doesn't",
        "didnt" to "didn't", "isnt" to "isn't", "wasnt" to "wasn't", "arent" to "aren't",
        "couldnt" to "couldn't", "shouldnt" to "shouldn't", "wouldnt" to "wouldn't",
        "dont" to "don't", "cant" to "can't", "wont" to "won't", "im" to "I'm", "ive" to "I've",
        "id" to "I'd", "ill" to "I'll", "i" to "I"
    )

    private val nextWords = mapOf(
        "i" to listOf("am", "will", "can", "have", "think"),
        "you" to listOf("are", "can", "will", "have", "should"),
        "the" to listOf("best", "new", "same", "first", "next"),
        "we" to listOf("can", "are", "will", "should", "need"),
        "please" to listOf("send", "let", "check", "confirm", "share"),
        "thank" to listOf("you", "you!"),
        "how" to listOf("are", "can", "do", "much", "long"),
        "what" to listOf("is", "are", "do", "does", "about"),
        "can" to listOf("you", "we", "i", "help", "please"),
        "let" to listOf("me", "us", "them"),
        "good" to listOf("morning", "afternoon", "evening", "job"),
        "see" to listOf("you", "this", "the", "what")
    )

    fun correct(word: String): String {
        val replacement = corrections[word.lowercase(Locale.ROOT)] ?: return word
        return if (word.firstOrNull()?.isUpperCase() == true) replacement.replaceFirstChar { it.uppercase() } else replacement
    }

    fun suggestions(prefix: String, previousWord: String, learned: List<String> = emptyList()): List<String> {
        val normalized = prefix.trim().lowercase(Locale.ROOT)
        val candidates = if (normalized.isBlank()) nextWords[previousWord.trim().lowercase(Locale.ROOT)].orEmpty() else corrections.keys
            .filter { it.startsWith(normalized) }
            .mapNotNull { corrections[it] }
        return (learned + candidates + nextWords[previousWord.trim().lowercase(Locale.ROOT)].orEmpty())
            .distinct()
            .filter { normalized.isBlank() || it.startsWith(normalized, ignoreCase = true) }
            .take(8)
    }

    fun correctSentence(text: String): String = text.split(Regex("(?<=\\s)|(?=[,.!?])")).joinToString("") { token ->
        if (token.any { it.isLetter() } && token.all { it.isLetter() || it == '\'' }) correct(token) else token
    }

    fun punctuationSpacing(text: String): String = text
        .replace(Regex("\\s+([,.!?;:])"), "$1")
        .replace(Regex("([,.!?;:])(?=[A-Za-z])"), "$1 ")

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

    fun polish(text: String): String = capitalize(punctuationSpacing(correctSentence(text))).trim()
}
