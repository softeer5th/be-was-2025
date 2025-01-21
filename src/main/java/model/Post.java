package model;

public class Post {
    private int id;
    private final String contents;
    private final String author;

    public Post(int id, String contents, String author) {
        this.id = id;
        this.contents = contents;
        this.author = author;
    }

    public Post(String contents, String author) {
        this.contents = contents;
        this.author = author;
    }

    public String getContents() {
        return contents;
    }

    public int getId() {
        return id;
    }
}
