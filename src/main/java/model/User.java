package model;

import Entity.QueryParameters;

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

    public static void validateUserParameters(QueryParameters queryParameters) {
        Set<String> keys = queryParameters.getKeySet();
        if (!keys.contains("userId") || !keys.contains("password") || !keys.contains("name") || !keys.contains("email")) {
            throw new IllegalArgumentException("User 정보 중 누락된 항목이 존재합니다.");
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

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
