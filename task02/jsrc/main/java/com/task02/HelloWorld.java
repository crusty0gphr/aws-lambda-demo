package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
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
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Gson gson = new Gson();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent request, Context context) {
        // Extract HTTP method and path from the request
        String httpMethod = "GET";
        String path = request.getRawPath();

        // Decide based on path and method
        if ("GET".equalsIgnoreCase(httpMethod) && "/hello".equalsIgnoreCase(path)) {
            return handleGetHello();
        } else {
            return generateBadRequestResponse(httpMethod, path);
        }
    }

    private APIGatewayV2HTTPResponse handleGetHello() {
        // Response body for success
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("statusCode", 200);
        responseBody.put("message", "Hello from Lambda");

        // Setup the response
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(200);  // Explicitly for clarity
        response.setBody(gson.toJson(responseBody));  // Converts the Map to a JSON string

        return response;
    }

    private APIGatewayV2HTTPResponse generateBadRequestResponse(String method, String path) {
        // Response body for error
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("statusCode", 400);
        responseBody.put("message", "Bad request syntax or unsupported method. Request path: " + path + ". HTTP method: " + method);

        // Setup a 400 Bad Request response
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(400);
        response.setBody(gson.toJson(responseBody));

        return response;
    }
}