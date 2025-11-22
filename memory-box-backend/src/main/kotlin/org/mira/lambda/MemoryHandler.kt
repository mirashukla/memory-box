package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.mira.dynamodb.MemoryBoxTable
import org.mira.lambda.AuthenticationHandler.Companion.mapToCreateUserRequest
import org.mira.lambda.AuthenticationHandler.Companion.mapToGetUserRequest
import org.mira.lambda.MemoriesHandler.Companion.mapToCreateMemoryRequest
import org.mira.lambda.MemoriesHandler.Companion.mapToGetMemoryRequest
import org.mira.lambda.ResponseHelper.response
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class MemoryHandler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayV2HTTPResponse> {

    private val region = System.getenv("AWS_REGION") ?: "eu-west-1"
    private val dynamoDbClient = DynamoDbClient.builder()
        .region(Region.of(region))
        .build()

    private val memoryBoxTable by lazy { MemoryBoxTable(dynamoDbClient) }
    private val memoriesHandler by lazy { MemoriesHandler(memoryBoxTable) }
    private val authenticationHandler by lazy { AuthenticationHandler() }

    override fun handleRequest(
        request: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        context.logger.log("Received request: ${request.httpMethod} ${request.path}\n")

        return try {
            when (request.path) {
                "/memories" -> handleMemories(request, context)
                "/auth" -> handleAuthentication(request, context)
                else -> response(404, """{"error":"Not Found"}""")
            }
        } catch (e: Exception) {
            context.logger.log("Error: ${e.message}\n${e.stackTraceToString()}")
            response(500, """{"error":"Internal Server Error"}""")
        }
    }

    fun handleMemories(
        request: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {
        val action = Memories.fromMethod(request.httpMethod)

        return when (action) {
            is Memories.Post -> memoriesHandler.handlePostMemory(mapToCreateMemoryRequest(request))
            is Memories.Get -> memoriesHandler.handleGetMemoriesPaginated(mapToGetMemoryRequest(request))
            is Memories.Unknown -> response(405, """{"error":"Method not allowed"}""")
        }
    }

    fun handleAuthentication(
        request: APIGatewayProxyRequestEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val action = Authentication.fromMethod(request.httpMethod)

        return when (action) {
            is Authentication.Post -> authenticationHandler.handleRegister(mapToCreateUserRequest(request))
            is Authentication.Get -> authenticationHandler.handleSignIn(mapToGetUserRequest(request))
            is Authentication.Unknown -> response(405, """{"error":"Method not allowed"}""")
        }
    }

}
