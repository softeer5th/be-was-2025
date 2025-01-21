package domain;

import db.AbstractDao;
import db.Database;
import db.Transaction;
import db.TransactionalDao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao extends AbstractDao implements TransactionalDao<UserDao> {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS users 
            (userId VARCHAR(20), passwordHash VARCHAR(200), name VARCHAR(20), email VARCHAR(50), PRIMARY KEY(userId))
            """;
    private static final String UPSERT_USER = """
            MERGE INTO users (userId, name, passwordHash, email)
            KEY (userId)
            VALUES (?, ?, ?, ?)
            """;
    private static final String SELECT_USER_BY_ID = """
            SELECT userId, name, passwordHash, email
            FROM users
            WHERE userId = ?
            """;
    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    public void initTable() {
        executeUpdate(i -> i == 0, CREATE_TABLE);
    }

    public boolean saveUser(User user) {
        return executeUpdate(i -> i > 0, UPSERT_USER, user.getUserId(), user.getName(), user.getPasswordHash(), user.getEmail());
    }

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


    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString("userId"),
                resultSet.getString("passwordHash"),
                resultSet.getString("name"),
                resultSet.getString("email")
        );
    }
}
