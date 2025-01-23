package model;

/**
 * The type User.
 */
public class User {
    private String userId;
    private String password;
    private String name;
    private String email;
    private byte[] image;

    /**
     * Instantiates a new User.
     *
     * @param userId the user id
     * @param password the password
     * @param name the name
     * @param email the email
     * @param image the image
     */
    public User(String userId, String password, String name, String email, byte[] image) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    /**
     * Validate password.
     *
     * @param password the password
     */
    public void validatePassword(String password) {
        if (!this.password.equals(password)) {
            throw new IllegalArgumentException("Password is invalid");
        }
    }

    /**
     * Update user.
     *
     * @param name the name
     * @param image the image
     * @param password the password
     * @param passwordConfirm the password confirm
     */
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

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Get image byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }
}
