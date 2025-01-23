package db;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

import static db.DBUtils.close;
import static db.DBUtils.getConnection;

public enum UserDao {
    USERS;


    private static final Logger log = LoggerFactory.getLogger(UserDao.class);


    /**
     * id와 매칭되는 user 를 찾아주는 메소드
     * @param userId 찾고자 하는 user ID
     * @return Optional 로 감싼 User 객체 반환. 해당 ID가 존재하지 않을 경우, 빈 Optional 객체 반환
     */
    public Optional<User> findById(String userId) {
        String sql = "select * from users where user_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, userId);
            resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                String findUserId = resultSet.getString("user_id");
                String findPassword = resultSet.getString("password");
                String findUsername = resultSet.getString("username");
                String findEmail = resultSet.getString("email");
                return Optional.of(new User(findUserId, findPassword, findUsername, findEmail));
            }
        } catch (SQLException e) {
            log.error("find 예외: ", e);
        }finally {
            close(resultSet, pstmt, con);
        }
        return Optional.empty();
    }

    /**
     * 새 회원을 저장하는 메소드
     * @param user
     * @return 생성된 회원 객체 반환
     */
    public Optional<User> save(User user){
        String sql = "insert into users(user_id, password, username, email) values (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());
            pstmt.executeUpdate();
            return Optional.of(user);
        } catch (SQLException e) {
            log.error("save 예외: ", e);
        }finally {
            close(null, pstmt, con);
        }
        return Optional.empty();
    }
}
