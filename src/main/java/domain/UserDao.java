package domain;

import db.AbstractDao;
import db.Database;
import db.Transaction;
import db.TransactionalDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * 사용자 정보를 데이터베이스에 저장하거나 조회하는 클래스
 */
public class UserDao extends AbstractDao implements TransactionalDao<UserDao> {
    private static final String UPSERT_USER = """
            MERGE INTO users (userId, name, passwordHash, email, profileImagePath)
            KEY (userId)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String SELECT_USER_BY_ID = """
            SELECT *
            FROM users
            WHERE userId = ?
            """;
    private final Database database;

    /**
     * UserDao 객체를 생성한다.
     *
     * @param database 사용할 데이터베이스
     */
    public UserDao(Database database) {
        this.database = database;
    }

    static User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("userId"),
                resultSet.getString("passwordHash"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("profileImagePath")
        );
    }


    /**
     * 사용자 정보를 삽입, 저장한다.
     *
     * @param user 사용자 정보
     * @return 저장에 성공하면 true, 실패하면 false
     */
    public boolean saveUser(User user) {
        return executeUpdate(i -> i > 0, UPSERT_USER, user.getUserId(), user.getName(), user.getPasswordHash(), user.getEmail(), user.getProfileImagePath());
    }

    /**
     * 사용자 정보를 조회한다.
     *
     * @param userId 사용자 식별자
     * @return 사용자 정보. 사용자가 존재하지 않으면 빈 Optional
     */
    public Optional<User> findUserById(String userId) {
        return executeQuery(rs -> {
            if (!rs.next())
                return Optional.empty();
            return Optional.of(mapUser(rs));
        }, SELECT_USER_BY_ID, userId);

    }


    @Override
    public UserDao joinTransaction(Transaction transaction) {
        return new UserDao(null) {
            @Override
            protected Connection getConnection() {
                return transaction.getConnection();
            }

            @Override
            protected boolean closeConnection() {
                return false;
            }
        };
    }

    @Override
    protected Connection getConnection() {
        return database.getConnection();
    }

    @Override
    protected boolean closeConnection() {
        return true;
    }

}
