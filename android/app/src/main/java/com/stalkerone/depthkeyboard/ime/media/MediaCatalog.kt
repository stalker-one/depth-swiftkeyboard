package com.stalkerone.depthkeyboard.ime.media

object MediaCatalog {
    val emojiCategories = linkedMapOf(
        "Recent" to emptyList(),
        "Smileys" to listOf("😀","😃","😄","😁","😂","🤣","😊","😍","🥰","😘"),
        "People" to listOf("👍","👎","👏","🙏","🙌","💪","🤝","👋","👌","✌️"),
        "Animals" to listOf("🐶","🐱","🐭","🐹","🐰","🦊","🐻","🐼","🐨","🐯"),
        "Food" to listOf("🍎","🍊","🍋","🍉","🍇","🍓","🍕","🍔","🍟","🍰"),
        "Travel" to listOf("🚗","🚕","🚌","🚆","✈️","🚀","🏠","🏖️","🗺️","⛺"),
        "Objects" to listOf("📱","💻","⌚","🎧","📷","💡","🔑","🎁","📌","✏️"),
        "Symbols" to listOf("❤️","💯","⭐","✅","❌","⚠️","🔥","✨","💬","❗")
    )
    fun search(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emojiCategories.values.flatten().distinct().take(30)
        return emojiCategories.entries.filter { it.key.lowercase().contains(q) }.flatMap { it.value }.distinct().take(30)
    }
}
