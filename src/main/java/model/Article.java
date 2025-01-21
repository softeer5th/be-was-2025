package model;

public class Article {
    private String id;
    private String userId;
    private String content;
    private String image_path;

    public Article(String id, String userId, String content, String image_path) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.image_path = image_path;
    }

    public Article(String id, String userId, String content) {
        this.id = id;
        this.userId = userId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getContent(){
        return content;
    }

    public String getImage_path(){
        return image_path;
    }

}
