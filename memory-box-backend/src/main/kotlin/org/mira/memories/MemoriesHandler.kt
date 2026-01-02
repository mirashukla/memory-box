package org.mira.memories

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.dynamodb.MemoryBoxTable
import org.mira.lambda.CreateMemoryRequest
import org.mira.lambda.GetAllMemoriesRequest
import org.mira.lambda.ResponseHelper

class MemoriesHandler(val memoryBoxTable: MemoryBoxTable) {

    companion object {
        fun mapToCreateMemoryRequest(requestBody: String): CreateMemoryRequest {
            return Json.decodeFromString<CreateMemoryRequest>(requestBody)
        }

        fun mapToGetMemoryRequest(request: APIGatewayV2HTTPEvent): GetAllMemoriesRequest {

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
            return GetAllMemoriesRequest(getPageSize(pageSize), pageToken)
        }
    }

    fun handlePostMemory(
        request: CreateMemoryRequest,
    ): APIGatewayV2HTTPResponse {
        memoryBoxTable.saveMemory(request.email, request.memoryItem)
        return ResponseHelper.response(200, """{"message":"Memory added!"}""")
    }

    fun handleGetAllMemoriesPaginated(
        request: GetAllMemoriesRequest
    ): APIGatewayV2HTTPResponse {
        val result = memoryBoxTable.getAllLatestMemories(
            pageSize = request.pageSize,
            pageToken = request.pageToken
        )
        return ResponseHelper.response(200, Json.encodeToString(result))
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