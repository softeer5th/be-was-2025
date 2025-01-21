package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private String profileImagePath;

    public User(String userId, String password, String name, String email, String profileImagePath) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImagePath = profileImagePath;
    }

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
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
