package com.stalkerone.depthkeyboard

/** Provider-neutral contract for optional cloud AI features.
 * Implementations should keep API keys off-device where possible.
 */
interface AiProvider {
    suspend fun rewrite(text: String, tone: String): String
    suspend fun chat(prompt: String): String
    suspend fun translate(text: String, language: String): String
    suspend fun search(query: String): String
}

/** No-op provider used until the user configures an approved backend. */
object DisabledAiProvider : AiProvider {
    override suspend fun rewrite(text: String, tone: String): String = text
    override suspend fun chat(prompt: String): String = "AI provider is not configured."
    override suspend fun translate(text: String, language: String): String = text
    override suspend fun search(query: String): String = "Search provider is not configured."
}
