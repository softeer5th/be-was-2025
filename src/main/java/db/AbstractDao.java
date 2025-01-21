package db;

import util.DatabaseUtil;
import util.ExceptionUtil;

import java.sql.Connection;
import java.sql.ResultSet;

// 공통적인 DAO 기능을 제공하는 추상 클래스
public abstract class AbstractDao {

    // 삽입 쿼리 실행. 삽입된 행의 Generated ID를 function에 전달
    protected void executeInsert(ExceptionUtil.CheckedConsumer<Long> function, String sql, Object... params) {
        DatabaseUtil.run(pstmt -> {
                    pstmt.executeUpdate();
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (!rs.next())
                        throw new RuntimeException("Generated key 가 없습니다");
                    function.accept(rs.getLong(1));
                    return null;
                },
                getConnection(),
                closeConnection(),
                sql, params);
    }

    // 조회 쿼리 실행
    protected <R> R executeQuery(ExceptionUtil.CheckedFunction<ResultSet, R> function, String sql, Object... params) {
        return DatabaseUtil.run(pstmt -> function.apply(pstmt.executeQuery()),
                getConnection(),
                closeConnection(),
                sql, params);
    }

    // 삽입, 수정, 삭제 쿼리 실행
    protected <R> R executeUpdate(ExceptionUtil.CheckedFunction<Integer, R> function, String sql, Object... params) {
        return DatabaseUtil.run(pstmt -> function.apply(pstmt.executeUpdate()),
                getConnection(),
                closeConnection(),
                sql, params);
    }


    // 쿼리를 실행할 Connection 객체를 반환. 서브 클래스에서 구현
    protected abstract Connection getConnection();

    // Connection 객체를 닫아야 하는지 여부를 반환. 서브 클래스에서 구현
    protected abstract boolean closeConnection();
}
