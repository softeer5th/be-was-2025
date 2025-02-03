package model;

import Entity.QueryParameters;
import db.Database;
import exception.DuplicateUserIdException;
import exception.MissingUserInfoException;

import java.util.Set;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static void validateSignUpUserParameters(QueryParameters queryParameters) {
        Set<String> keys = queryParameters.getKeySet();
        if (!keys.contains("userId") || !keys.contains("password") || !keys.contains("name") || !keys.contains("email")) {
            throw new MissingUserInfoException("회원가입 정보 중 누락된 항목이 존재합니다.");
        }
    }

    public static void validateSignInUserParameters(QueryParameters queryParameters) {
        Set<String> keys = queryParameters.getKeySet();
        if (!keys.contains("userId") || !keys.contains("password")) {
            throw new MissingUserInfoException("로그인 정보 중 누락된 항목이 존재합니다.");
        }
    }

    public static void validateSignUpUserIdDuplication(User user) {
        if (Database.findUserById(user.userId).isPresent()) {
            throw new DuplicateUserIdException("이미 존재하는 user id 입니다.");
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
