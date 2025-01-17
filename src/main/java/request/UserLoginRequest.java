package request;

import exception.ClientErrorException;
import util.HttpRequestParser;

import java.util.Map;

import static exception.ErrorCode.INVALID_FORM;

public record UserLoginRequest(
        String userId,
        String password
) {
    public static UserLoginRequest of(String paramString) {
        Map<String, String> paramMap = HttpRequestParser.parseParamString(paramString);
        return new UserLoginRequest(
                getOrElseThrow(paramMap, "userId"),
                getOrElseThrow(paramMap, "password")
        );
    }

    private static String getOrElseThrow(Map<String, String> map, String key) {
        if (!map.containsKey(key))
            throw new ClientErrorException(INVALID_FORM);
        return map.get(key);
    }
}
