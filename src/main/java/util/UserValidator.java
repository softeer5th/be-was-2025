package util;

import exception.ClientErrorException;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static exception.ErrorCode.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class UserValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,10}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9\\uAC00-\\uD7A3]{1,10}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+={}\\[\\]:;\"'<>,.?/-]).{8,20}$");

    public static void validateUser(String id, String nickname, String password, String email) {
        validateId(id);
        validateEmail(email);
        validateNickname(nickname);
        validatePassword(password);
    }

    private static void validatePassword(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(URLDecoder.decode(password, UTF_8));
        if (!matcher.matches()) {
            throw new ClientErrorException(INVALID_PASSWORD_FORMAT);
        }
    }

    private static void validateId(String id) {
        Matcher matcher = ID_PATTERN.matcher(URLDecoder.decode(id, UTF_8));
        if (!matcher.matches()) {
            throw new ClientErrorException(INVALID_USERID_FORMAT);
        }
    }

    private static void validateNickname(String nickname) {
        Matcher matcher = NAME_PATTERN.matcher(URLDecoder.decode(nickname, UTF_8));
        if (!matcher.matches()) {
            throw new ClientErrorException(INVALID_NICKNAME_FORMAT);
        }
    }

    private static void validateEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(URLDecoder.decode(email, UTF_8));
        if (!matcher.matches()) {
            throw new ClientErrorException(INVALID_EMAIL_FORMAT);
        }
    }

}
