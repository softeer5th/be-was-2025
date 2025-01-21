package db;

import exception.ServerErrorException;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static exception.ErrorCode.ERROR_WITH_DATABASE;

public class PostDatabase {
    private static final Logger logger = LoggerFactory.getLogger(PostDatabase.class);

    private static PostDatabase instance;

    private PostDatabase() {

    }

    public static PostDatabase getInstance() {
        if (instance == null) {
            instance = new PostDatabase();
        }
        return instance;
    }

    public int addPost(Post post) {
        String query = "INSERT INTO post (content, created_at, author) VALUES (?, ? ,?)";

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


    public List<Post> findAll() {
        String query = "SELECT * FROM post order by created_at desc";
        List<Post> postList = new ArrayList<>();

        try (Connection conn = DBConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Post post = new Post(rs.getInt("id"), rs.getString("content"),rs.getString("author"),rs.getTimestamp("created_at").toLocalDateTime());
                postList.add(post);
            }
        } catch (SQLException e) {
            throw new ServerErrorException(ERROR_WITH_DATABASE);
        }

        return postList;
    }
}
