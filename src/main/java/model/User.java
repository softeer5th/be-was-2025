package model;

public class User {
    public static String USER_ID = "userId";
    public static String USERNAME = "name";
    public static String PASSWORD = "password";
    public static String EMAIL = "email";
    private String userId;
    private String password;
    private String name;
    private String email;
    private String profileImage;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        profileImage = "/favicon.ico";
    }

    public User(String userId, String password, String name, String email, String profileImage) {
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

    public String getProfileImage() {
        return profileImage;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeProfileImage(String profileImage){
        this.profileImage = profileImage;
    }
}
