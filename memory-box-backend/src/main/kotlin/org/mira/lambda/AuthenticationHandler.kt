package org.mira.lambda

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.dynamodb.UsersTable
import org.mira.lambda.ResponseHelper.response


class AuthenticationHandler(val usersTable: UsersTable) {

    companion object {
        fun mapToCreateUserRequest(requestBody: String): CreateUserRequest {
            return Json.decodeFromString<CreateUserRequest>(requestBody)
        }

        fun mapToGetUserRequest(requestBody: String): GetUserRequest {
            return Json.decodeFromString<GetUserRequest>(requestBody)
        }
    }

    fun handleRegister(request: CreateUserRequest, logger: LambdaLogger?): APIGatewayV2HTTPResponse {

        return try {
            logger?.log("adding user")
            usersTable.registerUser(request.email, request.password)
            logger?.log("user added")
            response(200, "Success user added")
        } catch (exception: Exception) {
            logger?.log("Error: $exception")
            response(
                500,
                """{"error":"Failed to register user"}"""
            )
        }
    }

    fun handleSignIn(request: GetUserRequest): APIGatewayV2HTTPResponse {
        return response(200, "Success")
    }
}