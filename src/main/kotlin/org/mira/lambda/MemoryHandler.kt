package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import org.mira.dagger.DaggerAppComponent
import org.mira.dynamodb.MemoryBoxTable
import javax.inject.Inject

class MemoryHandler @Inject constructor() : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayV2HTTPResponse> {
    @Inject
    lateinit var memoryBoxTable: MemoryBoxTable

    init {
        DaggerAppComponent.create().inject(this)
    }

    override fun handleRequest(request: APIGatewayProxyRequestEvent, context: Context): APIGatewayV2HTTPResponse {
        val method = request.httpMethod
        val path = request.path

        context.logger.log("Method: $method, Path: $path, Body: ${request.body}\n")

        return when {
            path == "/memories" -> when (Memories.fromMethod(method)) {
                Memories.Post -> postMemory("testUser", "random memory")

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
        val memories = memoryBoxTable.getLatestMemories()
        return response(200, """{"memories": "${memories.joinToString { "," }}"}""")
    }

    private fun response(status: Int, body: String): APIGatewayV2HTTPResponse =
        APIGatewayV2HTTPResponse.builder()
            .withStatusCode(status)
            .withHeaders(mapOf("Content-Type" to "application/json"))
            .withBody(body)
            .build()
}