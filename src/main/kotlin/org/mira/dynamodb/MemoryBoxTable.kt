package org.mira.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryBoxTable @Inject constructor(private val dynamoDbClient: DynamoDbClient) {
    private companion object {
        const val MEMORY_BOX_TABLE_NAME = "MemoryBoxTable"
        const val USERNAME_ATTRIBUTE = "username"
        const val CREATED_AT_ATTRIBUTE = "createdAt"
        const val MEMORY_ATTRIBUTE = "memory"
    }

    fun saveMemory(username: String, memory: String) {
        val createdAt = Instant.now().toString()
        val request = PutItemRequest.builder().tableName(MEMORY_BOX_TABLE_NAME).item(
            mapOf(
                USERNAME_ATTRIBUTE to AttributeValue.builder().s(username).build(),
                CREATED_AT_ATTRIBUTE to AttributeValue.builder().s(createdAt).build(),
                MEMORY_ATTRIBUTE to AttributeValue.builder().s(memory).build()
            )
        ).build()
        dynamoDbClient.putItem(request)
    }

    fun getLatestMemories(page: Int = 0): List<Memory> {

        val queryRequest = QueryRequest.builder()
            .tableName(MEMORY_BOX_TABLE_NAME)
            .scanIndexForward(false)
            .limit(10)
            .build()

        val response = dynamoDbClient.query(queryRequest)
        val latestMemories = response.items()

        return latestMemories.map { convertMemoryEntry(it) }
    }

    private fun convertMemoryEntry(memoryEntry: Map<String, AttributeValue>): Memory =
        Memory(
            username = memoryEntry["username"]?.s() ?: "",
            createdAt = memoryEntry["createdAt"]?.s() ?: "",
            memory = memoryEntry["memory"]?.s() ?: ""
        )

}

