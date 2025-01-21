package util;

import exception.BaseException;
import exception.UserErrorCode;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,16}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]{2,10}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]{8,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    public static boolean isValidUserId(String userId) {
        return userId != null && USER_ID_PATTERN.matcher(userId).matches();
    }

    public static boolean isValidNickname(String nickname) {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void validateUser(String userId, String nickname, String password, String email) {
        if (!isValidUserId(userId)) {
            throw new BaseException(UserErrorCode.INVALID_USER_ID);
        }
        if (!isValidNickname(URLDecoder.decode(nickname, StandardCharsets.UTF_8))) {
            throw new BaseException(UserErrorCode.INVALID_NICKNAME);
        }
        if (!isValidPassword(password)) {
            throw new BaseException(UserErrorCode.INVALID_PASSWORD);
        }
        if (!isValidEmail(URLDecoder.decode(email, StandardCharsets.UTF_8))) {
            throw new BaseException(UserErrorCode.INVALID_EMAIL);
        }
    }
}
