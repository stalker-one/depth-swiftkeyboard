package com.stalkerone.depthkeyboard.ime.extensions

interface DepthExtension {
    val id: String
    val name: String
    val version: Int
    fun onLoad()
    fun onUnload()
}

class ExtensionRegistry {
    private val extensions = linkedMapOf<String, DepthExtension>()
    fun register(extension: DepthExtension) { extensions[extension.id] = extension; extension.onLoad() }
    fun unregister(id: String) { extensions.remove(id)?.onUnload() }
    fun list(): List<DepthExtension> = extensions.values.toList()
    fun contains(id: String) = extensions.containsKey(id)
}
