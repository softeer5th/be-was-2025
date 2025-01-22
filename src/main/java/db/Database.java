package db;
import model.User;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String JDBC_URL = "jdbc:h2:./new_file;DB_CLOSE_ON_EXIT=TRUE";
    // 파일 기반 H2 데이터베이스
    private static final String USER = "";
    private static final String PASSWORD = "";
    static {
        initializeDatabase();
    }

    // 데이터베이스 초기화
    private static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            logger.debug("database initialized");
            Statement stmt = connection.createStatement();

            // 테이블 삭제
            stmt.execute("DROP TABLE IF EXISTS \"user\"");

            // 테이블 생성
            String createUserTable = "CREATE TABLE \"user\" ("
                    + "\"userId\" VARCHAR(255) PRIMARY KEY, "
                    + "password VARCHAR(255), "
                    + "name VARCHAR(255), "
                    + "email VARCHAR(255)"
                    + ")";
            stmt.execute(createUserTable);

            stmt.close();
        } catch (SQLException e) {
            logger.error("initialize error, {}", e.getMessage());
        }
    }

    public static void addPost(Post post) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {

            // 새로운 post 추가
            String insertQuery = "INSERT INTO post (post_id, title, content, user_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, post.getPostId());  // 새로 생성된 post_id
                insertStmt.setString(2, post.getTitle());
                insertStmt.setString(3, post.getContent());
                insertStmt.setString(4, post.getUserId());
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("addPost error, {}", e.getMessage());
        }
    }

    public static int getLastPostId() {
        String getLastPostIdQuery = "SELECT post_id FROM POST ORDER BY post_id DESC LIMIT 1";
        int lastPostId = 0;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement lastPostStmt = connection.prepareStatement(getLastPostIdQuery)) {

            ResultSet rs = lastPostStmt.executeQuery();
            if (rs.next()) {
                lastPostId = rs.getInt("post_id");
            }
        } catch (SQLException e) {
            logger.error("getLastPostId error, {}", e.getMessage());
        }

        return lastPostId;
    }


    // 사용자 추가 (DB에 저장)
    public static void addUser(User user) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String insertQuery = "INSERT INTO \"user\" (\"userId\", password, name, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, user.getUserId());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getName());
                stmt.setString(4, user.getEmail());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("addUser error, {}", e.getMessage());
        }
        }
    }

    // 사용자 ID로 사용자 조회
    public static Optional<User> findUserById(String userId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String selectQuery = "SELECT * FROM \"user\" WHERE \"userId\" = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    User user = new User(
                            rs.getString("userId"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // 모든 사용자 조회
    public static Collection<User> findAll() {
        Collection<User> userList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            String selectQuery = "SELECT * FROM \"user\"";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(selectQuery);
                while (rs.next()) {
                    User user = new User(
                            rs.getString("userId"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    );
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            logger.error("findAllUser error, {}", e.getMessage());
        }
        return userList;
    }
}
