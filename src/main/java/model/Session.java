package model;

import java.time.LocalTime;
import java.util.UUID;

public class Session {
    private String sessionId;
    private String userId;
    private LocalTime lastAccessTime;
    private int maxInactiveInterval;

    public Session(String userId, int maxInactiveInterval) {
        this.sessionId = UUID.randomUUID().toString();
        this.userId = userId;
        this.lastAccessTime = LocalTime.now();
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public Session(String sessionId, String userId, LocalTime lastAccessTime, int maxInactiveInterval) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.lastAccessTime = lastAccessTime;
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalTime getLastAccessTime() {
        return lastAccessTime;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setLastAccessTime(LocalTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
}
