package com.stalkerone.depthkeyboard.ime.translator

object TranslationEngine {
    fun prompt(targetLanguage: String, text: String): String =
        "Translate to $targetLanguage. Preserve meaning, punctuation and line breaks. Return only the translation.\n$text"

    fun supportedLanguages(): List<String> = listOf(
        "English", "Urdu", "Arabic", "Spanish", "French", "German", "Italian", "Portuguese",
        "Turkish", "Dutch", "Polish", "Russian", "Hindi", "Bengali", "Chinese", "Japanese", "Korean"
    )
}
