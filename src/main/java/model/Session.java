package model;

public record Session (String sessionId, String userId){

    public Session(String sessionId, String userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }
}
