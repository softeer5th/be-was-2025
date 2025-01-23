package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private byte[] profileImage;

    public User(String userId, String password, String name, String email, byte[] profileImage) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
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

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setName(String newName) {
        this.name = newName;
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}