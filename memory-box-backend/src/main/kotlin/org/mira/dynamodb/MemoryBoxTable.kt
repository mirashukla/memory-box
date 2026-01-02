package org.mira.dynamodb

import kotlinx.serialization.json.Json
import org.mira.lambda.MemoryItem
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
        const val EMAIL_ATTRIBUTE = "email"
        const val CREATED_AT_ATTRIBUTE = "createdAt"
        const val MEMORY_ATTRIBUTE = "memory"
    }

    fun saveMemory(email: String, memoryItem: MemoryItem) {
        val item = mapOf(
            EMAIL_ATTRIBUTE to AttributeValue.fromS(email),
            CREATED_AT_ATTRIBUTE to AttributeValue.fromS(Instant.now().toString()),
            MEMORY_ATTRIBUTE to AttributeValue.fromS(Json.encodeToString<MemoryItem>(memoryItem))
        )

        val request = PutItemRequest.builder()
            .tableName(MEMORY_BOX_TABLE_NAME)
            .item(item)
            .build()

        dynamoDbClient.putItem(request)
    }

    fun getLatestMemories(email: String, pageSize: Int = 10, pageToken: String?): MemoriesResponse {

        fun fetchPage(createdAtToken: CreatedAt?, pageSize: Int): List<Memory> {
            val query = QueryRequest.builder()
                .tableName(MEMORY_BOX_TABLE_NAME)
                .scanIndexForward(false)
                .keyConditionExpression("email = :email")
                .expressionAttributeValues(
                    mapOf(":email" to AttributeValue.builder().s(email).build())
                )
                .limit(pageSize)

            if (createdAtToken != null) {
                query.keyConditionExpression("createdAt <= :start")
                    .expressionAttributeValues(
                        mapOf(":start" to AttributeValue.fromS(createdAtToken.time.toString()))
                    )
            }

            return dynamoDbClient.query(query.build()).items().map { convertMemoryEntry(it) }
        }

        val page = paginate(
            pageSize = pageSize,
            pageToken = pageToken,
            decodeToken = { decodeToken<CreatedAt>(it) },
            encodeToken = { encodeToken(it) },
            fetchPage = { pageToken, limit -> fetchPage(pageToken, limit) },
            extractToken = { CreatedAt(Instant.parse(it.createdAt)) }
        )

        return MemoriesResponse(page.items, page.nextPageToken)
    }

    private fun convertMemoryEntry(entry: Map<String, AttributeValue>) = Memory(
        createdAt = entry[CREATED_AT_ATTRIBUTE]?.s() ?: "",
        memory = Json.decodeFromString<MemoryItem>(entry[MEMORY_ATTRIBUTE]?.s() ?: "")
    )
}
