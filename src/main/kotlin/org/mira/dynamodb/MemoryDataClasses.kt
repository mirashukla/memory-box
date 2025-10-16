package org.mira.dynamodb

import kotlinx.serialization.Serializable
import org.mira.lambda.MemoryItem

@Serializable
data class Memory(
    val username: String,
    val createdAt: String,
    val memory: MemoryItem
)

@Serializable
data class MemoriesResponse(
    val memories: List<Memory>,
    val nextPageToken: String?
)