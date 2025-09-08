package org.mira.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent

class MemoryHandler : RequestHandler<APIGatewayProxyRequestEvent, String> {
    override fun handleRequest(request: APIGatewayProxyRequestEvent, context: Context): String {
        context.logger.log("Received request: $request\n")
        return "Hello, Memory Box!"
    }
}