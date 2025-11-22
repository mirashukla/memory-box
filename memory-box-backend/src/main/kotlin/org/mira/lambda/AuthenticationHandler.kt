package org.mira.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.lambda.ResponseHelper.response


class AuthenticationHandler() {

    companion object {
        fun mapToCreateUserRequest(request: APIGatewayProxyRequestEvent): CreateUserRequest {
            val body = request.body
            return Json.decodeFromString<CreateUserRequest>(body)
        }

        fun mapToGetUserRequest(request: APIGatewayProxyRequestEvent): GetUserRequest {
            val body = request.body
            return Json.decodeFromString<GetUserRequest>(body)
        }
    }

    fun handleRegister(request: CreateUserRequest): APIGatewayV2HTTPResponse {
        return response(200, "Success")
    }

    fun handleSignIn(request: GetUserRequest): APIGatewayV2HTTPResponse {
        return response(200, "Success")
    }
}