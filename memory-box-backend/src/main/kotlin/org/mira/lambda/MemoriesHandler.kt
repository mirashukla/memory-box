package org.mira.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.dynamodb.MemoryBoxTable
import org.mira.lambda.ResponseHelper.response

class MemoriesHandler(val memoryBoxTable: MemoryBoxTable) {

    companion object {
        fun mapToCreateMemoryRequest(request: APIGatewayProxyRequestEvent): CreateMemoryRequest {
            val body = request.body
            return Json.decodeFromString<CreateMemoryRequest>(body)
        }

        fun mapToGetMemoryRequest(request: APIGatewayProxyRequestEvent): GetMemoriesRequest {

            fun getPageSize(pageSize: Int?): Int {
                return if (pageSize == null || pageSize < 1) {
                    10
                } else {
                    pageSize
                }
            }

            val queryStringParameters = request.queryStringParameters
            val pageSize = queryStringParameters["pageSize"]?.toIntOrNull()
            val pageToken = queryStringParameters["pageToken"]
            return GetMemoriesRequest(getPageSize(pageSize), pageToken)
        }
    }

    fun handlePostMemory(
        request: CreateMemoryRequest,
    ): APIGatewayV2HTTPResponse {
        memoryBoxTable.saveMemory(request.username, request.memoryItem)
        return response(200, """{"message":"Memory added!"}""")
    }

    fun handleGetMemoriesPaginated(
        request: GetMemoriesRequest
    ): APIGatewayV2HTTPResponse {
        val result = memoryBoxTable.getLatestMemories(pageSize = request.pageSize, pageToken = request.pageToken)
        return response(200, Json.encodeToString(result))
    }


//    {
//        "body": "{ \"username\": \"johndoe\", \"memoryItem\": { \"title\": \"My First Memory\", \"content\": \"I remember learning how to ride a bike when I was seven.\" } }",
//        "resource": "/{proxy+}",
//        "path": "/memories",
//        "httpMethod": "POST",
//        "isBase64Encoded": false
//    }

//    {
//        "resource": "/{proxy+}",
//        "path": "/memories",
//        "httpMethod": "GET",
//        "queryStringParameters": {
//        "pageSize": "4"
//    },
//        "isBase64Encoded": false
//    }
}