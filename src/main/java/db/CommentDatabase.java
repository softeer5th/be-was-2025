package db;

import exception.ServerErrorException;
import model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class CommentDatabase {
    private static final Logger logger = LoggerFactory.getLogger(CommentDatabase.class);

    private static CommentDatabase instance;

    private CommentDatabase() {
    }

    public static CommentDatabase getInstance() {
        if (instance == null) {
            instance = new CommentDatabase();
        }
        return instance;
    }

    public int addComment(Comment comment) {
        String query = "INSERT INTO comment (post_id, content, created_at, author) VALUES (?, ?, ? ,?)";

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, comment.getPostId());
            pstmt.setString(2, URLDecoder.decode(comment.getContents(), StandardCharsets.UTF_8));
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            pstmt.setString(4, URLDecoder.decode(comment.getAuthor(), StandardCharsets.UTF_8));


            final int id = pstmt.executeUpdate();
            logger.debug("Add comment" + comment);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }


    public List<Comment> findAllByPostId(int postId) {
        String query = "SELECT * FROM comment WHERE post_id = ? ORDER BY created_at";
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, postId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
        return comments;
    }
}
