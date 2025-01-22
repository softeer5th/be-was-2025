package db;

import db.transaction.Transaction;
import model.User;
import util.DBUtil;

import java.sql.*;
import java.util.Optional;

import static util.DBUtil.*;

public class UserDao {
    private static final UserDao INSTANCE = new UserDao();

    private UserDao() {
        String sql = """
                    CREATE TABLE Users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        login_id VARCHAR(50) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100)
                    )
                    """;
        try{
            Connection con = getConnection();
            Statement stmt = con.createStatement();

            stmt.executeUpdate(sql);

            release(stmt, null);
        }catch (SQLException e) {
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }
    }

    public static UserDao getInstance(){
        return INSTANCE;
    }

    public void save(Transaction transaction, User user) {
        String sql = "INSERT INTO Users (login_id, password, name, email) VALUES (?, ?, ?, ?)";

        try {
            Connection con = transaction.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getLoginId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();
            release(pstmt, null);
        }catch(SQLException e){
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }
    }

    public Optional<User> findById(Transaction transaction, long id){
        String sql = "SELECT * FROM Users WHERE id = ?";

        try{
           Connection con = transaction.getConnection();
           PreparedStatement pstmt = con.prepareStatement(sql);
           pstmt.setLong(1, id);
           ResultSet rs = pstmt.executeQuery();

           if (rs.next()) {
               return Optional.of(parseUserFromResultSet(rs));
           }
           release(pstmt, rs);
       }catch(SQLException e){
           throw new IllegalStateException("INTERNAL SERVER ERROR", e);
       }

        return Optional.empty();
    }

    public Optional<User> findByLoginId(Transaction transaction, String loginId){
        String sql = "SELECT * FROM Users WHERE login_id = ?";
        try{
            Connection con = transaction.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);

            pstmt.setString(1, loginId);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return Optional.of(parseUserFromResultSet(rs));
            }
            release(pstmt, rs);
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
        return Optional.empty();
    }

    public Optional<User> findByName(Transaction transaction, String name){
        String sql = "SELECT * From Users WHERE name = ?";

        try{
            Connection con = transaction.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                return Optional.of(parseUserFromResultSet(rs));
            }
            release(pstmt, rs);
        }catch (SQLException e){
            throw new IllegalStateException("INTERNAL SERVER ERROR", e);
        }

        return Optional.empty();
    }

    private User parseUserFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String loginId = rs.getString("login_id");
        String password = rs.getString("password");
        String name = rs.getString("name");
        String email = rs.getString("email");

        return new User(id, loginId, password, name, email);
    }
}
