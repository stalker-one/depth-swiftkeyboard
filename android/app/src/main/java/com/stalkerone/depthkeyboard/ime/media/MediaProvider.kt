package com.stalkerone.depthkeyboard.ime.media

interface MediaProvider {
    val id: String
    val displayName: String
    fun search(query: String): List<MediaItem>
}

data class MediaItem(val id: String, val title: String, val uri: String, val animated: Boolean = false)

class LocalStickerProvider : MediaProvider {
    override val id = "local-stickers"
    override val displayName = "Local stickers"
    override fun search(query: String) = emptyList<MediaItem>()
}

class ExternalMediaRegistry {
    private val providers = mutableListOf<MediaProvider>()
    fun register(provider: MediaProvider) { if (providers.none { it.id == provider.id }) providers += provider }
    fun all(): List<MediaProvider> = providers.toList()
    fun search(query: String): List<MediaItem> = providers.flatMap { it.search(query) }
}
