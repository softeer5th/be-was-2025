package request;

import exception.ClientErrorException;
import util.HttpRequestParser;

import java.util.Map;

import static exception.ErrorCode.MISSING_FIELD;

public record UserCreateRequest(
        String userId,
        String nickname,
        String password,
        String email
) {
    public static UserCreateRequest of(String paramString) {
        Map<String, String> paramMap = HttpRequestParser.parseParamString(paramString);
        return new UserCreateRequest(
                getOrElseThrow(paramMap, "userId"),
                getOrElseThrow(paramMap, "nickname"),
                getOrElseThrow(paramMap, "password"),
                getOrElseThrow(paramMap, "email")
        );
    }

    private static String getOrElseThrow(Map<String, String> map, String key) {
        if (!map.containsKey(key))
            throw new ClientErrorException(MISSING_FIELD);
        return map.get(key);
    }
}
