package org.mira.dynamodb

import org.mira.utils.PaginationToken.CreatedAt
import org.mira.utils.PaginationUtils.decodeToken
import org.mira.utils.PaginationUtils.encodeToken
import org.mira.utils.Paginator.paginate
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import software.amazon.awssdk.services.dynamodb.model.QueryRequest
import java.time.Instant

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

    data class MemoriesResponse(val memories: List<Memory>, val nextPageToken: String?)

    fun getLatestMemories(pageSize: Int = 10, pageToken: String?): MemoriesResponse {

        fun fetchPage(pageToken: CreatedAt?, pageSize: Int): List<Memory> {
            val query = QueryRequest.builder()
                .tableName(MEMORY_BOX_TABLE_NAME)
                .scanIndexForward(false)
                .limit(pageSize)

            if (pageToken != null) {
                query.keyConditionExpression("createdAt <= :start")
                    .expressionAttributeValues(
                        mapOf(":start" to AttributeValue.fromS(pageToken.time.toString()))
                    )
            }

            return dynamoDbClient.query(query.build()).items().map { convertMemoryEntry(it) }
        }


        val paginatedResponse = paginate(
            pageSize = pageSize,
            pageToken = pageToken,
            decodeToken = { decodeToken<CreatedAt>(it) },
            encodeToken = { encodeToken(it) },
            fetchPage = { pageToken, limit -> fetchPage(pageToken, limit) },
            extractToken = { CreatedAt(Instant.parse(it.createdAt)) }
        )

        return MemoriesResponse(paginatedResponse.items, paginatedResponse.nextPageToken)
    }


    private fun convertMemoryEntry(memoryEntry: Map<String, AttributeValue>): Memory =
        Memory(
            username = memoryEntry["username"]?.s() ?: "",
            createdAt = memoryEntry["createdAt"]?.s() ?: "",
            memory = memoryEntry["memory"]?.s() ?: ""
        )

}

