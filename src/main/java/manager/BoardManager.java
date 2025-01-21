package manager;

import db.PostDatabase;
import model.Post;

import java.util.List;

public class BoardManager {
    private final PostDatabase postDatabase;
    private static BoardManager instance;


    private BoardManager() {

        postDatabase = PostDatabase.getInstance();
    }

    public static BoardManager getInstance() {
        if (instance == null) {
            instance = new BoardManager();
        }
        return instance;
    }

    public void save(String content, String author) {
        Post post = new Post(content, author);
        postDatabase.addPost(post);
    }

    public List<Post> getPosts() {
        return postDatabase.findAll();
    }
}
