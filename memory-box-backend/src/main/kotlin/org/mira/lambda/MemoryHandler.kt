package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.mira.dynamodb.MemoryBoxTable
import org.mira.dynamodb.UsersTable
import org.mira.lambda.AuthenticationHandler.Companion.mapToCreateUserRequest
import org.mira.lambda.AuthenticationHandler.Companion.mapToGetUserRequest
import org.mira.lambda.MemoriesHandler.Companion.mapToCreateMemoryRequest
import org.mira.lambda.MemoriesHandler.Companion.mapToGetMemoryRequest
import org.mira.lambda.ResponseHelper.response
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class MemoryHandler : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private val region = System.getenv("AWS_REGION") ?: "eu-west-1"
    private val dynamoDbClient = DynamoDbClient.builder()
        .region(Region.of(region))
        .build()

    private val memoryBoxTable by lazy { MemoryBoxTable(dynamoDbClient) }
    private val usersTable by lazy { UsersTable(dynamoDbClient) }
    private val memoriesHandler by lazy { MemoriesHandler(memoryBoxTable) }
    private val authenticationHandler by lazy { AuthenticationHandler(usersTable) }

    override fun handleRequest(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val method = request.requestContext?.http?.method
        val path = request.requestContext?.http?.path

        context.logger.log("Received request: $method $path.\n")

        return try {
            when (path) {
                "/memories" -> handleMemories(request, context)
                "/auth/register" -> handleRegistration(request, context)
                "/auth/login" -> handleAuthentication(request, context)
                else -> response(404, """{"error":"Not Found"}""")
            }
        } catch (e: Exception) {
            context.logger.log("Error: ${e.message}\n${e.stackTraceToString()}")
            response(500, """{"error":"Internal Server Error"}""")
        }
    }

    fun handleMemories(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {
        val action = Memories.fromMethod(request.requestContext.http.method)

        return when (action) {
            is Memories.Post -> memoriesHandler.handlePostMemory(mapToCreateMemoryRequest(request.body))
            is Memories.Get -> memoriesHandler.handleGetMemoriesPaginated(mapToGetMemoryRequest(request))
            is Memories.Unknown -> response(405, """{"error":"Method not allowed"}""")
        }
    }

    fun handleRegistration(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val method = request.requestContext.http.method

        return if (method == "POST")
            authenticationHandler.handleRegister(
                mapToCreateUserRequest(request.body),
                context.logger
            )
        else response(405, """{"error":"Method not allowed"}""")
    }

    fun handleAuthentication(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val action = Authentication.fromMethod(request.requestContext.http.method)

        return when (action) {
            is Authentication.Get -> authenticationHandler.handleSignIn(mapToGetUserRequest(request.body))
            else -> response(405, """{"error":"Method not allowed"}""")
        }
    }

}
