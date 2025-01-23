package request;

import exception.ClientErrorException;
import util.UserValidator;
import util.HttpRequestParser;

import java.util.Map;

import static exception.ErrorCode.*;

public record UserCreateRequest(
        String userId,
        String nickname,
        String password,
        String email
) {
    public static UserCreateRequest of(String paramString) {
        Map<String, String> paramMap = HttpRequestParser.parseParamString(paramString);
        final String userId = getOrElseThrow(paramMap, "userId");
        final String email = getOrElseThrow(paramMap, "email");
        final String nickname = getOrElseThrow(paramMap, "nickname");
        final String password = getOrElseThrow(paramMap, "password");

        UserValidator.validateUser(userId, nickname, password, email);

        return new UserCreateRequest(
                userId,
                nickname,
                password,
                email
        );
    }

    private static String getOrElseThrow(Map<String, String> map, String key) {
        if (!map.containsKey(key))
            throw new ClientErrorException(MISSING_FIELD);
        return map.get(key);
    }
}
