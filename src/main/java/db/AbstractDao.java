package db;

import util.DatabaseUtil;
import util.ExceptionUtil;

import java.sql.Connection;
import java.sql.ResultSet;


/**
 * 데이터베이스 접근을 위한 추상 클래스
 */
public abstract class AbstractDao {

    /**
     * 삽입 쿼리 실행
     *
     * @param function 삽입된 행의 Generated ID를 전달받을 함수
     * @param sql      실행할 쿼리
     * @param params   쿼리에 전달할 파라미터
     */
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

    /**
     * 조회 쿼리 실행
     *
     * @param function 조회 결과를 전달받을 함수
     * @param sql      실행할 쿼리
     * @param params   쿼리에 전달할 파라미터
     * @param <R>      function의 반환값의 타입
     * @return function의 반환값
     */
    protected <R> R executeQuery(ExceptionUtil.CheckedFunction<ResultSet, R> function, String sql, Object... params) {
        return DatabaseUtil.run(pstmt -> function.apply(pstmt.executeQuery()),
                getConnection(),
                closeConnection(),
                sql, params);
    }

    /**
     * 삽입, 수정, 삭제 쿼리 실행
     *
     * @param function 삽입, 수정, 삭제된 행의 개수를 전달받을 함수
     * @param sql      실행할 쿼리
     * @param params   쿼리에 전달할 파라미터
     * @param <R>      function의 반환값의 타입
     * @return function의 반환값
     */
    protected <R> R executeUpdate(ExceptionUtil.CheckedFunction<Integer, R> function, String sql, Object... params) {
        return DatabaseUtil.run(pstmt -> function.apply(pstmt.executeUpdate()),
                getConnection(),
                closeConnection(),
                sql, params);
    }


    /**
     * 쿼리를 실행할 때 사용할 Connection 객체를 반환. 서브 클래스에서 구현
     *
     * @return Connection 객체. 트랜젝션 미사용 시 매번 새로운 Connection 객체를 반환하도록 구현해야 함. 트랜젝션 사용 시 항상 트랜젝션 내에서 사용할 Connection 객체를 반환하도록 구현해야 함
     */
    protected abstract Connection getConnection();

    /**
     * 쿼리 실행 후 Connection을 닫을지 여부를 반환. 서브 클래스에서 구현
     *
     * @return Connection을 닫을지 여부. 트랜젝션 미사용 시 false, 사용 시 true를 반환하도록 구현해야 함
     */
    protected abstract boolean closeConnection();
}
