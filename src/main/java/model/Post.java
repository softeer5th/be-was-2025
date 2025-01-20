package model;

public class Post {
    private int id;
    private String contents;

    public Post(int id, String contents) {
        this.id = id;
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public int getId() {
        return id;
    }
}
