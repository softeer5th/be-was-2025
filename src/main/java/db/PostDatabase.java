package db;

import exception.ServerErrorException;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class PostDatabase {
    private static final Logger logger = LoggerFactory.getLogger(PostDatabase.class);
    private static PostDatabase instance;
    private static final String TABLE_NAME = "post";
    private static final int PAGE_SIZE = 1;

    private PostDatabase() {
    }

    public static PostDatabase getInstance() {
        if (instance == null) {
            instance = new PostDatabase();
        }
        return instance;
    }

    public int addPost(Post post) {
        String query = String.format("INSERT INTO %s (content, created_at, author) VALUES (?, ? ,?)", TABLE_NAME);

        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, post.getContents());
            pstmt.setTimestamp(2, Timestamp.from(Instant.now()));
            pstmt.setString(3, URLDecoder.decode(post.getAuthor(), StandardCharsets.UTF_8));

            final int id = pstmt.executeUpdate();
            logger.debug("Add post" + post);
            return id;

        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    public Post getPost(int page) {
        if (page < 1) {
            page = 1;
        }

        try (Connection conn = DBConnectionManager.getConnection()) {
            String query = String.format("SELECT * FROM %s ORDER BY id DESC LIMIT ? OFFSET ?", TABLE_NAME);
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, PAGE_SIZE);
            pstmt.setInt(2, page - 1);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post(
                            rs.getInt("id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    logger.debug("Fetched post: " + post);
                    return post;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }

    public int getTotalPages() {
        String query = String.format("SELECT COUNT(*) AS total_count FROM %s", TABLE_NAME);

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int totalCount = rs.getInt("total_count");
                return (int) Math.ceil((double) totalCount / PAGE_SIZE);
            }
            return 0;
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }
    }
}
