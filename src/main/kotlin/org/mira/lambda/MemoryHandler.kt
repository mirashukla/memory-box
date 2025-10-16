package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.dynamodb.MemoryBoxTable
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class MemoryHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayV2HTTPResponse> {

    private val region = System.getenv("AWS_REGION") ?: "eu-west-1"
    private val dynamoDbClient = DynamoDbClient.builder()
        .region(Region.of(region))
        .build()

    private val memoryBoxTable by lazy { MemoryBoxTable(dynamoDbClient) }

    override fun handleRequest(
        request: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        context.logger.log("Received request: ${request.httpMethod} ${request.path}\n")

        return try {
            when (request.path) {
                "/memories" -> handleMemories(request, context)
                else -> response(404, """{"error":"Not Found"}""")
            }
        } catch (e: Exception) {
            context.logger.log("Error: ${e.message}\n${e.stackTraceToString()}")
            response(500, """{"error":"Internal Server Error"}""")
        }
    }

    private fun handleMemories(
        request: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {
        return when (request.httpMethod.uppercase()) {
            "POST" -> {
                val body = request.body ?: return response(400, """{"error":"Missing body"}""")
                val memoryRequest = Json.decodeFromString<CreateMemoryRequest>(body)
                context.logger.log("Saving memory for user: ${memoryRequest.username}")
                memoryBoxTable.saveMemory(memoryRequest)
                response(200, """{"message":"Memory added!"}""")
            }

            "GET" -> {
                val pageToken = request.queryStringParameters?.get("pageToken")
                val result = memoryBoxTable.getLatestMemories(pageSize = 10, pageToken = pageToken)
                response(200, Json.encodeToString(result))
            }

            else -> response(405, """{"error":"Method not allowed"}""")
        }
    }

    private fun response(status: Int, body: String): APIGatewayV2HTTPResponse =
        APIGatewayV2HTTPResponse.builder()
            .withStatusCode(status)
            .withHeaders(mapOf("Content-Type" to "application/json"))
            .withBody(body)
            .build()
}
