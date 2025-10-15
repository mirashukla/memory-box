package org.mira.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Instant
import java.util.Base64

class MemoryBoxTable(private val dynamoDbClient: DynamoDbClient) {
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

    data class MemoriesResponse(val memories: List<Memory>, val nextPageToken: String)

    fun getLatestMemories(pageSize: Int = 10, pageToken: String): MemoriesResponse {
        val queryRequest = QueryRequest.builder()
            .tableName(MEMORY_BOX_TABLE_NAME)
            .scanIndexForward(false)
            .limit(pageSize + 1)

        if (pageToken.isNotBlank()) {
            val decodedPageToken = decodeToken(pageToken)
            val expressionValues = mutableMapOf<String, AttributeValue>(
                ":createdAt" to AttributeValue.fromS(decodedPageToken)
            )
            queryRequest
                .keyConditionExpression("createdAt <= :start")
                .expressionAttributeValues(expressionValues)
        }

        val response = dynamoDbClient.query(queryRequest.build())
        val latestMemories = response.items()
        val convertedMemories = latestMemories.map { convertMemoryEntry(it) }
        val nextPageToken =
            if (convertedMemories.size <= pageSize || convertedMemories.isEmpty()) "" else convertedMemories.last().createdAt

        return MemoriesResponse(convertedMemories, encodeToken(nextPageToken))
    }

    private fun decodeToken(pageToken: String): String =
        String(Base64.getUrlDecoder().decode(pageToken))

    private fun encodeToken(nextPageToken: String): String {
        return Base64.getUrlEncoder().encode(nextPageToken.toByteArray()).toString()
    }

    private fun convertMemoryEntry(memoryEntry: Map<String, AttributeValue>): Memory =
        Memory(
            username = memoryEntry["username"]?.s() ?: "",
            createdAt = memoryEntry["createdAt"]?.s() ?: "",
            memory = memoryEntry["memory"]?.s() ?: ""
        )

}

