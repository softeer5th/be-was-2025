package domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HashUtil;

public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    private static final String PASSWORD_SALT = "yTC2%fdK9@vQ";
    private String userId;
    private String passwordHash;
    private String name;
    private String email;

    // for deserialization
    private User() {
    }

    User(String userId, String passwordHash, String name, String email) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
    }

    public static User create(String userId, String password, String name, String email) {
        return new User(userId, hashPassword(password), name, email);
    }

    private static String hashPassword(String password) {
        String saltedPassword = password + PASSWORD_SALT;
        return HashUtil.hash(saltedPassword);
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isPasswordCorrect(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    public void update(String name, String newPassword) {
        this.name = name;
        this.passwordHash = hashPassword(newPassword);
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", name=" + name + ", email=" + email + "]";
    }


    String getPasswordHash() {
        return passwordHash;
    }
}
