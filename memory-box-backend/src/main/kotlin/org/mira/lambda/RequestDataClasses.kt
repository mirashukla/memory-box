package org.mira.lambda

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemoryRequest(val username: String, val memoryItem: MemoryItem)

@Serializable
data class MemoryItem(val title: String, val content: String)

@Serializable
data class GetMemoriesRequest(val pageSize: Int, val pageToken: String?)

@Serializable
data class GetUserRequest(val username: String, val password: String)

@Serializable
data class CreateUserRequest(val username: String, val password: String)