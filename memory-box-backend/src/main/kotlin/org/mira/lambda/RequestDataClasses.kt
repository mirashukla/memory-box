package org.mira.lambda

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemoryRequest(val email: String, val memoryItem: MemoryItem, val accessToken: String)

@Serializable
data class MemoryItem(val title: String, val content: String)

@Serializable
data class GetAllMemoriesRequest(val pageSize: Int, val pageToken: String?)

@Serializable
data class GetUserRequest(val email: String, val password: String)

@Serializable
data class CreateUserRequest(val email: String, val password: String)