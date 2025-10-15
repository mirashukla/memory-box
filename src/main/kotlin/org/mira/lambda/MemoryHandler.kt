package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.mira.dynamodb.MemoryBoxTable
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.lang.Exception

class MemoryHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayV2HTTPResponse> {
    private val region = System.getenv("AWS_REGION")
    private val dynamoDbClient = DynamoDbClient.builder().region(Region.of(region)).build()
    private val memoryBoxTable by lazy { MemoryBoxTable(dynamoDbClient) }

    override fun handleRequest(request: APIGatewayProxyRequestEvent, context: Context): APIGatewayV2HTTPResponse {
        val method = request.httpMethod
        val path = request.path

        context.logger.log("Method: $method, Path: $path, Body: ${request.body}\n")

        return when {
            path == "/memories" -> when (Memories.fromMethod(method)) {
                Memories.Post -> {
                    try {
                        context.logger.log("Lambda running in region: $region\n")
                        postMemory("testUser", "random memory")
                    } catch (e: Exception) {
                        context.logger.log(e.toString())
                        throw e
                    }
                }

                Memories.Get -> getMemories()
                else -> response(405, """{"error":"Method not allowed"}""")
            }

            else -> response(404, """{"error":"Not Found"}""")
        }
    }

    private fun postMemory(user: String, memory: String): APIGatewayV2HTTPResponse {
        memoryBoxTable.saveMemory(user, memory)
        return response(200, """{"message": "Memory added!"}""")
    }

    private fun getMemories(): APIGatewayV2HTTPResponse {
        val memories = memoryBoxTable.getLatestMemories(
            pageSize = 10,
            pageToken = ""
        ).memories
        return response(200, """{"memories": "${memories.joinToString { "," }}"}""")
    }

    private fun response(status: Int, body: String): APIGatewayV2HTTPResponse =
        APIGatewayV2HTTPResponse.builder()
            .withStatusCode(status)
            .withHeaders(mapOf("Content-Type" to "application/json"))
            .withBody(body)
            .build()
}