package request;

import exception.ClientErrorException;
import exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;

import static exception.ErrorCode.INVALID_FORM;

public record UserCreateRequest(
        String userId,
        String nickname,
        String password,
        String email
) {
    public static UserCreateRequest of(String paramString) {
        Map<String, String> paramMap = parseParamString(paramString);
        return new UserCreateRequest(
                getOrElseThrow(paramMap, "userId"),
                getOrElseThrow(paramMap, "nickname"),
                getOrElseThrow(paramMap, "password"),
                getOrElseThrow(paramMap, "email")
        );
    }

    private static String getOrElseThrow(Map<String, String> map, String key) {
        if (!map.containsKey(key))
            throw new ClientErrorException(INVALID_FORM);
        return map.get(key);
    }

    private static Map<String, String> parseParamString(String paramString) {
        String[] params = paramString.split("&");
        Map<String, String> paramMap = new HashMap<>();

        for (String param : params) {
            String[] nameAnyKey = param.split("=");
            if (nameAnyKey.length != 2) {
                throw new ClientErrorException(ErrorCode.MISSING_FIELD);
            }
            paramMap.put(nameAnyKey[0], nameAnyKey[1]);
        }
        return paramMap;
    }
}
