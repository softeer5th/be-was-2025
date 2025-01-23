package model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Post {
    private int postId;
    private String userId;
    private int ImageId;
    private String title;
    private String content;

    public Post(String userId, int imageId, String title, String content) {
        this.userId = userId;
        this.ImageId = imageId;
        this.title = title;
        this.content = content;
    }

    public Post(int postId, int ImageId, String userId, String title, String content) {
        this.postId = postId;
        this.ImageId = ImageId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public int getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return URLDecoder.decode(title, StandardCharsets.UTF_8);
    }

    public String getContent() {
        return URLDecoder.decode(content, StandardCharsets.UTF_8);
    }
}
