package domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HashUtil;

/**
 * 유저를 나타내는 클래스
 */
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

    /**
     * 새로운 유저를 생성한다.
     *
     * @param userId   아이디. 중복되면 안된다.
     * @param password 비밀번호
     * @param name     이름
     * @param email    이메일
     * @return 생성된 유저
     */
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

    /**
     * 비밀번호가 맞는지 확인한다.
     *
     * @param password 비밀번호
     * @return 맞으면 true, 틀리면 false
     */
    public boolean isPasswordCorrect(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * 유저 정보를 업데이트한다.
     *
     * @param name        새로운 이름
     * @param newPassword 새로운 비밀번호
     */
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
