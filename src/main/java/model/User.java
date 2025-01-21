package model;

import db.Database;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class User {

    private String userId;
    private String password;
    private String nickname;
    private String email;

    public User(String userId, String nickname, String password, String email) {
        this.userId = userId;
        this.nickname = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
        this.password = password;
        this.email = URLDecoder.decode(email, StandardCharsets.UTF_8);
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", nickname=" + nickname + ", email="
                + email + "]";
    }

    public void registerUser() {
        Database.addUser(this);
    }
}
