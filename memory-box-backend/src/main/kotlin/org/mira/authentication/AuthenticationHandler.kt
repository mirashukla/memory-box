package org.mira.authentication

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.json.Json
import org.mira.dynamodb.UserRepository
import org.mira.lambda.CreateUserRequest
import org.mira.lambda.GetUserRequest
import org.mira.lambda.ResponseHelper

class AuthenticationHandler(val userRepository: UserRepository, val passwordService: PasswordService) {

    companion object {
        fun mapToCreateUserRequest(requestBody: String): CreateUserRequest {
            return Json.Default.decodeFromString<CreateUserRequest>(requestBody)
        }

        fun mapToGetUserRequest(requestBody: String): GetUserRequest {
            return Json.Default.decodeFromString<GetUserRequest>(requestBody)
        }
    }


    fun handleRegister(request: CreateUserRequest, logger: LambdaLogger?): APIGatewayV2HTTPResponse {

        return try {
            logger?.log("adding user")
            val user = userRepository.getUser(request.email)
            if (user == null) {
                val hashAndSalt = passwordService.generateHashAndSalt(request.password)
                userRepository.registerUser(request.email, hashAndSalt.hashHex, hashAndSalt.saltBase64)
                logger?.log("user added")
                ResponseHelper.response(200, "Success user added")
            } else {
                return ResponseHelper.response(
                    403,
                    """{"error":"User already exists"}"""
                )
            }
        } catch (exception: Exception) {
            logger?.log("Error: $exception")
            ResponseHelper.response(
                500,
                """{"error":"Failed to register user"}"""
            )
        }
    }

    fun handleSignIn(request: GetUserRequest): APIGatewayV2HTTPResponse {
        val userAuthInfo =
            userRepository.getUserAuthentication(request.email) ?: return ResponseHelper.response(404, "User not found")

        val successHash = passwordService.verifyPassword(
            password = request.password,
            storedHashHex = userAuthInfo.hash,
            storedSaltBase64 = userAuthInfo.salt
        )
        return if (successHash)
            ResponseHelper.response(200, "Success")
        else ResponseHelper.response(403, "Incorrect Password")
    }
}