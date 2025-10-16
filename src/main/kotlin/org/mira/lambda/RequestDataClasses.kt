package org.mira.lambda

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemoryRequest(val username: String, val memoryItem: MemoryItem)

@Serializable
data class MemoryItem(val title: String, val content: String)