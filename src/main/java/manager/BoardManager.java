package manager;

import db.PostDatabase;
import model.Post;
import util.AutoKeyGenerator;

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

    public void save(String title){
        Post post = new Post(AutoKeyGenerator.getKey(), title);
        postDatabase.addPost(post);
    }

    public List<Post> getPosts(){
        return postDatabase.findAll();
    }
 }
