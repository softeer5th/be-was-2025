package model;

import db.Database;

public class User {

    private String userId;
    private String password;
    private String nickname;
    private String email;

    public User(String userId, String nickname, String password, String email) {
        this.userId = userId;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
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
