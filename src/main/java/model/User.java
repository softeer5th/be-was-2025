package model;

import webserver.HTTPExceptions;

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

    // 회원가입 시 이메일 등록을 하지 않은 경우
    // 추후 이메일 등록이 필수일 경우 삭제 필요
    public User(String userId, String password, String name) {
        if (userId == null || password == null || name == null || userId.isEmpty() || password.isEmpty() || name.isEmpty()) {
            throw new HTTPExceptions.Error400("400 Bad Request: Missing required parameters");
        }

        this.userId = userId;
        this.password = password;
        this.name = name;
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
