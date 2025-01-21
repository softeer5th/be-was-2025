package manager;

import db.CommentDatabase;
import exception.ClientErrorException;
import model.Comment;

import java.util.List;

import static exception.ErrorCode.EXCEED_POST_LENGTH;
import static exception.ErrorCode.MISSING_INPUT;

public class CommentManager {
    private final CommentDatabase commentDatabase;
    private static CommentManager instance;


    private CommentManager() {
        commentDatabase = CommentDatabase.getInstance();
    }

    public static CommentManager getInstance() {
        if (instance == null) {
            instance = new CommentManager();
        }
        return instance;
    }

    public void save(int postId, String content, String author) {
        if (content.length() > 500)
            throw new ClientErrorException(EXCEED_POST_LENGTH);
        if (content.isEmpty())
            throw new ClientErrorException(MISSING_INPUT);
        Comment comment = new Comment(postId, content, author);
        commentDatabase.addComment(comment);
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return commentDatabase.findAllByPostId(postId);
    }
}
