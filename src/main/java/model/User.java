package model;

import java.util.regex.Pattern;

public class User {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,16}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private static final String IDENTIFIER_REGEX = "^[a-zA-Z][a-zA-Z0-9]{7,15}$";
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEX);

    public Long id = null;
    public String userId;
    public String password;
    public String name;
    public String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public User(Long id, String userId, String password, String name, String email) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static boolean validateUserId(String userId) {
        return IDENTIFIER_PATTERN.matcher(userId).matches();
    }

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public Long getId() { return id; }

    public String getUserId() { return userId; }

    public String getPassword() { return password; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public void changePassword(String newPassword) { this.password = newPassword; }

    public void changeName(String newName) { this.name = newName; }
}
