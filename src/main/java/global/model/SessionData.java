package global.model;

import model.User;

public class SessionData {
    private final User user;
    private final long expirationTime;

    public SessionData(User user, long expirationTime) {
        this.user = user;
        this.expirationTime = expirationTime;
    }

    public User getUser() {
        return user;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}