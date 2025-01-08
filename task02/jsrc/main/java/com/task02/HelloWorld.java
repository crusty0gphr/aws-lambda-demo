package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
        // Extract the path and method from the request
        String path = (String) request.get("path");
        String method = (String) request.get("httpMethod");

        Map<String, Object> resultMap = new HashMap<>();

        // Check if the request is to the /hello endpoint with GET method
        if ("/hello".equals(path) && "GET".equalsIgnoreCase(method)) {
            resultMap.put("statusCode", 200);
            resultMap.put("body", "{\"message\": \"Hello from Lambda\"}");
        } else {
            resultMap.put("statusCode", 400);
            resultMap.put("body", String.format(
                    "{\"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
                    path, method));
        }

        return resultMap;
    }
}
