package manager;

import db.PostBookMarkDatabase;
import db.PostDatabase;
import db.PostLikeDatabase;
import exception.ClientErrorException;
import model.Post;

import java.util.List;

import static exception.ErrorCode.*;

public class BoardManager {
    private final PostDatabase postDatabase;
    private final PostLikeDatabase postLikeDatabase;
    private final PostBookMarkDatabase postBookMarkDatabase;
    private static BoardManager instance;


    private BoardManager() {
        postDatabase = PostDatabase.getInstance();
        postLikeDatabase = PostLikeDatabase.getInstance();
        postBookMarkDatabase = PostBookMarkDatabase.getInstance();
    }

    public static BoardManager getInstance() {
        if (instance == null) {
            instance = new BoardManager();
        }
        return instance;
    }

    public void save(String content, String author) {
        if (content.length() > 500)
            throw new ClientErrorException(EXCEED_POST_LENGTH);
        if (content.isEmpty())
            throw new ClientErrorException(MISSING_INPUT);
        Post post = new Post(content, author);
        postDatabase.addPost(post);
    }

    public List<Post> getPosts() {
        return postDatabase.findAll();
    }

    public void likePost(int postId, int userId) {
        if (postLikeDatabase.existsPostLike(postId, userId))
            throw new ClientErrorException(ALREADY_LIKE_POST);
        postLikeDatabase.addPostLike(postId, userId);
    }

    public void bookmarkPost(int postId, int userId) {
        if (postBookMarkDatabase.existsBookMark(postId, userId))
            throw new ClientErrorException(ALREADY_MARK_POST);
        postBookMarkDatabase.addBookMark(postId, userId);
    }

    public boolean existsPostLike(int postId, int userId) {
        return postLikeDatabase.existsPostLike(postId, userId);
    }

    public boolean existsPostBookMark(int postId, int userId) {
        return postBookMarkDatabase.existsBookMark(postId, userId);
    }
}
