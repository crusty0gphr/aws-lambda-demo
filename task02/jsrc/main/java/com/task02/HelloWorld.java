package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
        lambdaName = "hello_world",
        roleName = "hello_world-role",
        isPublishVersion = false,
        aliasName = "${lambdas_alias_name}",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson gson = new Gson();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        // Extract HTTP method and path from the request
        String httpMethod = request.getHttpMethod();
        String path = request.getPath();

        // Decide based on path and method
        if ("GET".equalsIgnoreCase(httpMethod) && "/hello".equalsIgnoreCase(path)) {
            return handleGetHello();
        } else {
            return generateBadRequestResponse(httpMethod, path);
        }
    }

    private APIGatewayProxyResponseEvent handleGetHello() {
        // Response body for success
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("statusCode", 200);
        responseBody.put("message", "Hello from Lambda");

        // Setup the response
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);  // Explicitly for clarity
        response.setBody(gson.toJson(responseBody));  // Converts the Map to a JSON string

        return response;
    }

    private APIGatewayProxyResponseEvent generateBadRequestResponse(String method, String path) {
        // Response body for error
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("statusCode", 400);
        responseBody.put("message", "Bad request syntax or unsupported method. Request path: " + path + ". HTTP method: " + method);

        // Setup a 400 Bad Request response
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400);
        response.setBody(gson.toJson(responseBody));

        return response;
    }
}