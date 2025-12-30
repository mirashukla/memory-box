package org.mira.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse

object ResponseHelper {

    fun response(statusCode: Int, body: String): APIGatewayV2HTTPResponse =
        APIGatewayV2HTTPResponse.builder()
            .withStatusCode(statusCode)
            .withHeaders(mapOf("Content-Type" to "application/json"))
            .withBody(body)
            .build()
}