package model;

import java.util.Base64;

public class Image {
    private int id;
    private String userId;
    private String contentType;
    private byte[] data;
    private boolean forProfile;

    public Image(int id, String userId, String contentType, byte[] data, boolean isForProfile) {
        this.id = id;
        this.userId = userId;
        this.contentType = contentType;
        this.data = data;
        this.forProfile = isForProfile;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDataString() {
        return Base64.getEncoder().encodeToString(data);
    }
}
