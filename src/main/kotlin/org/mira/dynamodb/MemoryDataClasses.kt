package org.mira.dynamodb

import kotlinx.serialization.Serializable

@Serializable
data class Memory(
    val username: String,
    val createdAt: String,
    val memory: String
)

@Serializable
data class MemoriesResponse(
    val memories: List<Memory>,
    val nextPageToken: String?
)