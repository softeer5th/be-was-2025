package model;

import db.Database;

public class User {

    private String userId;
    private String password;
    private String nickname;
    private String email;

    public User(String userId, String password, String nickname, String email) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
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
