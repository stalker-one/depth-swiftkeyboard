package com.stalkerone.depthkeyboard

/** Local-first typing intelligence used by the keyboard service.
 *  This deliberately contains no network credentials or proprietary keyboard code.
 */
object FeatureEngine {
    private val replacements = mapOf(
        "teh" to "the", "adn" to "and", "dont" to "don't", "cant" to "can't",
        "wont" to "won't", "im" to "I'm", "ive" to "I've", "id" to "I'd",
        "i" to "I"
    )

    private val commonNext = mapOf(
        "i" to listOf("am", "will", "can", "have"),
        "you" to listOf("are", "can", "will", "have"),
        "the" to listOf("best", "new", "same", "first"),
        "we" to listOf("can", "are", "will", "should"),
        "please" to listOf("send", "let", "check", "confirm")
    )

    fun autocorrect(word: String): String =
        replacements[word.lowercase()]?.let { replacement ->
            if (word.firstOrNull()?.isUpperCase() == true)
                replacement.replaceFirstChar { it.uppercase() }
            else replacement
        } ?: word

    fun predictions(previousWord: String): List<String> =
        commonNext[previousWord.trim().lowercase()].orEmpty()

    fun capitalizeSentence(text: String): String {
        if (text.isBlank()) return text
        val index = text.indexOfFirst { !it.isWhitespace() }
        if (index < 0) return text
        return text.substring(0, index) + text[index].uppercaseChar() + text.substring(index + 1)
    }

    /** Turns two trailing spaces into sentence punctuation, matching familiar mobile keyboard behavior. */
    fun handleDoubleSpace(textBeforeCursor: String): String? {
        if (!textBeforeCursor.endsWith("  ")) return null
        return textBeforeCursor.dropLast(2) + ". "
    }

    fun tonePrompt(tone: String, text: String): String =
        "Rewrite the following text in a $tone tone. Preserve the meaning and return only the rewritten text:\n$text"

    fun translatePrompt(language: String, text: String): String =
        "Translate the following text to $language. Return only the translation:\n$text"
}
