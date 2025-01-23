package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private byte[] image;

    public User(String userId, String password, String name, String email, byte[] image) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public void validatePassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException("Password is invalid");
        }
    }

    public void updateUser(String name, byte[] image, String password, String passwordConfirm){

        if(!this.password.equals(password)){
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        if(!password.equals(passwordConfirm)){
            throw new IllegalArgumentException("입력 비밀번호가 서로 다릅니다.");
        }

        this.name = name;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
