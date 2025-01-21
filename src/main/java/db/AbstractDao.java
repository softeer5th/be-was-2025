package db;

import util.ExceptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static util.CommonUtil.close;

// 공통적인 DAO 기능을 제공하는 추상 클래스
public abstract class AbstractDao {

    // 삽입 쿼리 실행. 삽입된 행의 Generated ID를 function에 전달
    protected void executeInsert(ExceptionUtil.CheckedConsumer<Long> function, String sql, Object... params) {
        run(pstmt -> {
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                rs.next();
                function.accept(rs.getLong(1));
            }
            throw new RuntimeException("Generated key 가 없습니다");
        }, sql, params);
    }

    // 조회 쿼리 실행
    protected <R> R executeQuery(ExceptionUtil.CheckedFunction<ResultSet, R> function, String sql, Object... params) {
        return run(pstmt -> function.apply(pstmt.executeQuery()), sql, params);
    }

    // 삽입, 수정, 삭제 쿼리 실행
    protected <R> R executeUpdate(ExceptionUtil.CheckedFunction<Integer, R> function, String sql, Object... params) {
        return run(pstmt -> function.apply(pstmt.executeUpdate()), sql, params);
    }

    protected <R> R run(ExceptionUtil.CheckedFunction<PreparedStatement, R> function, String sql, Object... params) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return function.apply(pstmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(pstmt);
            if (closeConnection())
                close(connection);
        }
    }

    // 쿼리를 실행할 Connection 객체를 반환. 서브 클래스에서 구현
    protected abstract Connection getConnection();

    // Connection 객체를 닫아야 하는지 여부를 반환. 서브 클래스에서 구현
    protected abstract boolean closeConnection();
}
