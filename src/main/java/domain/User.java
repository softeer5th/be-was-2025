package domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HashUtil;
import webserver.request.FileUploader;

/**
 * 유저를 나타내는 클래스
 */
public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    /**
     * 비밀번호 해싱에 사용할 salt
     */
    private static final String PASSWORD_SALT = "yTC2%fdK9@vQ";
    /**
     * 유저 아이디
     */
    private String userId;
    /**
     * 비밀번호 해시값
     */
    private String passwordHash;
    /**
     * 이름
     */
    private String name;
    /**
     * 이메일
     */
    private String email;
    /**
     * 프로필 이미지 경로
     */
    private String profileImagePath;

    // for deserialization
    private User() {
    }

    User(String userId, String passwordHash, String name, String email, String profileImagePath) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.name = name;
        this.email = email;
        this.profileImagePath = profileImagePath;
    }

    /**
     * 새로운 유저를 생성한다. 비밀번호는 해싱된다.
     *
     * @param userId   아이디. 중복되면 안된다.
     * @param password 비밀번호
     * @param name     이름
     * @param email    이메일
     * @return 생성된 유저
     */
    public static User create(String userId, String password, String name, String email) {
        return new User(userId, hashPassword(password), name, email, null);
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

    public String getProfileImagePath() {
        return profileImagePath;
    }

    /**
     * 비밀번호가 맞는지 확인한다.
     *
     * @param password 비밀번호
     * @return 맞으면 true, 틀리면 false
     */
    public boolean isPasswordCorrect(String password) {
        log.debug("passwordHash: {}, inputPassword: {} ,inputPasswordHash: {}", passwordHash, password, hashPassword(password));
        return this.passwordHash.equals(hashPassword(password));
    }

    /**
     * 유저 정보를 업데이트한다.
     *
     * @param currentPassword  현재 비밀번호
     * @param name             새로운 이름. null이면 업데이트하지 않음
     * @param newPassword      새로운 비밀번호. null이면 업데이트하지 않음
     * @param profileImagePath 새로운 프로필 이미지 경로. null이면 업데이트하지 않음
     * @throws IllegalArgumentException 현재 비밀번호가 틀릴 때
     */
    public void update(String currentPassword, String name, String newPassword, String profileImagePath) {
        if (!isPasswordCorrect(currentPassword)) {
            log.error("password not matched");
            throw new IllegalArgumentException("password not matched");
        }
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (newPassword != null && !newPassword.isEmpty()) {
            this.passwordHash = hashPassword(newPassword);
        }
        if (profileImagePath != null) {
            this.profileImagePath = profileImagePath;
        }
    }

    /**
     * 프로필 이미지를 삭제한다.
     *
     * @param uploader 프로필 이미지 파일을 삭제할 때 사용할 FileUploader
     */
    public void deleteProfileImage(FileUploader uploader) {
        if (profileImagePath != null) {
            uploader.deleteFile(profileImagePath);
        }
        this.profileImagePath = null;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", name=" + name + ", email=" + email + "]";
    }


    String getPasswordHash() {
        return passwordHash;
    }
}
