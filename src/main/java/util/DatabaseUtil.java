package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static util.CommonUtil.close;

/**
 * 데이터베이스 작업을 위한 유틸리티 클래스
 */
public class DatabaseUtil {

    private static final Logger log = LoggerFactory.getLogger(DatabaseUtil.class);

    /**
     * PreparedStatement를 이용하여 데이터베이스 작업을 수행.
     * function을 실행한 후 PreparedStatement는 자동으로 닫히므로 function 내부에서 ResultSet을 닫을 필요 없음
     *
     * @param function        PreparedStatement를 이용하여 수행할 로직
     * @param connection      PreparedStatement를 생성할 Connection
     * @param closeConnection Connection을 닫을지 여부. transaction 내에서 사용할 경우 false로 설정
     * @param sql             PreparedStatement에 사용할 SQL 쿼리
     * @param params          SQL 쿼리에 바인딩할 파라미터
     * @param <R>             function의 반환값의 타입
     * @return function의 반환값
     */
    public static <R> R run(ExceptionUtil.CheckedFunction<PreparedStatement, R> function, Connection connection, boolean closeConnection, String sql, Object... params) {
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            log.debug("SQL: {}, params: {}", sql, params);
            return function.apply(pstmt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(pstmt);
            if (closeConnection)
                close(connection);
        }
    }
}
