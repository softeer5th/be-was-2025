package exception;

import http.HttpStatus;

import java.sql.SQLException;

public enum DBErrorCode implements ErrorCode {
    DUPLICATE_ENTRY(HttpStatus.CONFLICT, "데이터 중복 오류입니다."),
    INVALID_QUERY(HttpStatus.BAD_REQUEST, "잘못된 SQL 쿼리입니다."),
    DEADLOCK_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "데드락이 발생했습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 데이터를 참조했습니다."),
    CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB 연결에 실패했습니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB에 알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    DBErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }

    public static DBErrorCode mapSQLErrorCode(SQLException e) {
        return switch (e.getErrorCode()) {
            case 1003 -> DBErrorCode.DATA_NOT_FOUND;
            case 1062 -> DBErrorCode.DUPLICATE_ENTRY;
            case 1048 -> DBErrorCode.INVALID_QUERY;
            case 1213 -> DBErrorCode.DEADLOCK_ERROR;
            case 2003 -> DBErrorCode.CONNECTION_FAILED;
            default -> DBErrorCode.UNKNOWN_ERROR;
        };
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
