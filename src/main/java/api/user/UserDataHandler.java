package api.user;

import api.ApiHandler;
import db.TokenStore;
import exception.ErrorCode;
import exception.UserCreationException;
import model.RequestData;
import util.JsonUtil;
import webserver.http.HttpResponse;
import webserver.load.LoadResult;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserDataHandler implements ApiHandler {

    @Override
    public boolean canHandle(RequestData requestData) {
        return "GET".equalsIgnoreCase(requestData.method()) &&
                requestData.path().startsWith("/api/userdata");
    }

    @Override
    public LoadResult handle(RequestData requestData) {
        String queryString = extractQueryString(requestData.path());
        Map<String, String> params = parseQueryString(queryString);

        String token = params.get("token");
        if (token == null) {
            throw new UserCreationException(ErrorCode.TOKEN_MISSING);
        }

        UserData userData = TokenStore.get(token);
        if (userData == null) {
            throw new UserCreationException(ErrorCode.TOKEN_NOT_FOUND);
        }

        return createJsonLoadResult(convertUserDataToJson(userData));
    }

    private String convertUserDataToJson(UserData userData) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("userId", userData.userId());
        dataMap.put("name", userData.name());
        dataMap.put("password", userData.password());
        return JsonUtil.toJson(dataMap);
    }

    private LoadResult createJsonLoadResult(String json) {
        byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
        return new LoadResult(jsonBytes, "/api/userdata", "application/json");
    }

    private String extractQueryString(String path) {
        String[] parts = path.split("\\?", 2);
        if (parts.length < 2) {
            throw new UserCreationException(ErrorCode.TOKEN_MISSING);
        }
        return parts[1];
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> paramMap = new HashMap<>();
        for (String pair : queryString.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                paramMap.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return paramMap;
    }
}