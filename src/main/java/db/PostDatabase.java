package db;

import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostDatabase {
    private static final Logger logger = LoggerFactory.getLogger(PostDatabase.class);
    private final Map<Integer, Post> posts;

    private static PostDatabase instance;

    private PostDatabase() {
        posts = new HashMap<>();
    }

    public static PostDatabase getInstance() {
        if (instance == null) {
            instance = new PostDatabase();
        }
        return instance;
    }

    public void addPost(Post post) {
        logger.debug("Add post" + post);
        posts.put(post.getId(), post);
    }


    public List<Post> findAll() {
        return posts.values().stream().toList();
    }
}
