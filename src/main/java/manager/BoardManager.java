package manager;

import db.PostDatabase;
import db.PostLikeDatabase;
import exception.ClientErrorException;
import model.Post;

import java.util.List;

import static exception.ErrorCode.ALREADY_LIKE_POST;

public class BoardManager {
    private final PostDatabase postDatabase;
    private final PostLikeDatabase postLikeDatabase;
    private static BoardManager instance;


    private BoardManager() {
        postDatabase = PostDatabase.getInstance();
        postLikeDatabase = PostLikeDatabase.getInstance();
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

    public void likePost(int postId, int userId){
        if(postLikeDatabase.existsPostLike(postId, userId))
            throw new ClientErrorException(ALREADY_LIKE_POST);
        postLikeDatabase.addPostLike(postId, userId);
    }

    public boolean existsPostLike(int postId, int userId){
        return postLikeDatabase.existsPostLike(postId, userId);
    }
}
