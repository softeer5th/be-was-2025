package model;

public record Session (String userId){

    public Session(String userId) {
        this.userId = userId;
    }
}
