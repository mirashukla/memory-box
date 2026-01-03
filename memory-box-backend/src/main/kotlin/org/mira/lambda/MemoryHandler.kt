package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.mira.authentication.Authentication
import org.mira.authentication.AuthenticationHandler
import org.mira.dynamodb.MemoryBoxTable
import org.mira.dynamodb.UserRepository
import org.mira.authentication.AuthenticationHandler.Companion.mapToCreateUserRequest
import org.mira.authentication.AuthenticationHandler.Companion.mapToGetUserRequest
import org.mira.authentication.PasswordService
import org.mira.authentication.TokenGenerator
import org.mira.memories.MemoriesHandler.Companion.mapToCreateMemoryRequest
import org.mira.memories.MemoriesHandler.Companion.mapToGetMemoryRequest
import org.mira.lambda.ResponseHelper.response
import org.mira.memories.Memories
import org.mira.memories.MemoriesHandler
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.ssm.SsmClient
import software.amazon.awssdk.services.ssm.model.GetParameterRequest

class MemoryHandler : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private val region = System.getenv("AWS_REGION") ?: "eu-west-1"
    private val dynamoDbClient = DynamoDbClient.builder()
        .region(Region.of(region))
        .build()

    private val ssmClient by lazy {
        SsmClient.builder()
            .region(Region.of(region))
            .build()
    }

    private val memoryBoxTable by lazy { MemoryBoxTable(dynamoDbClient) }
    private val userRepository by lazy { UserRepository(dynamoDbClient) }
    private val memoriesHandler by lazy { MemoriesHandler(memoryBoxTable) }
    private val passwordService by lazy { PasswordService() }
    private val tokenGenerator by lazy { TokenGenerator(secret) }
    private val authenticationHandler by lazy { AuthenticationHandler(userRepository, passwordService, tokenGenerator) }
    private val secretName = System.getenv("AUTH_SECRET_NAME")
    private val secret by lazy { fetchSecret() }

    override fun handleRequest(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val method = request.method()
        val path = request.path()

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

    private fun extractBearerToken(headers: Map<String, String>): String? {
        val authHeader = headers["authorization"] ?: headers["Authorization"]
        return authHeader?.takeIf { it.startsWith("Bearer ") }?.substringAfter("Bearer ")?.trim()
    }

    fun handleMemories(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {
        val action = Memories.fromMethod(request.requestContext.http.method)
        val bearerToken = extractBearerToken(request.headers)
        if (!tokenGenerator.validate(bearerToken)) return response(
            401,
            """{"error":"Invalid or missing token"}"""
        )
        return when (action) {
            is Memories.Post -> memoriesHandler.handlePostMemory(mapToCreateMemoryRequest(request.body))
            is Memories.Get -> memoriesHandler.handleGetAllMemoriesPaginated(mapToGetMemoryRequest(request))
            is Memories.Unknown -> response(405, """{"error":"Method not allowed"}""")
        }
    }

    fun handleRegistration(
        request: APIGatewayV2HTTPEvent,
        context: Context
    ): APIGatewayV2HTTPResponse {

        val method = request.method()

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

        val action = Authentication.fromMethod(request.method())

        return when (action) {
            is Authentication.Post -> authenticationHandler.handleSignIn(mapToGetUserRequest(request.body))
            else -> response(405, """{"error":"Method not allowed"}""")
        }
    }

    private fun APIGatewayV2HTTPEvent.method(): String? = this.requestContext?.http?.method
    private fun APIGatewayV2HTTPEvent.path(): String? = this.requestContext?.http?.path

    private fun fetchSecret(): String {
        val request = GetParameterRequest.builder()
            .name(secretName)
            .withDecryption(true)
            .build()

        val response = ssmClient.getParameter(request)

        return response.parameter().value()
    }

}
