package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private int profileImageId;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        profileImageId = -1;
    }

    public User(String userId, String password, String name, String email, int profileImageId) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImageId = profileImageId;
    }

    public void setName(String name) {
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

    public int getProfileImageId() { return profileImageId; }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
