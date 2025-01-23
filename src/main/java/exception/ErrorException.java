package exception;

import enums.HttpStatus;

/**
 * 애플리케이션에서 발생할 수 있는 오류를 처리하는 예외 클래스입니다.
 * <p>
 * 이 클래스는 {@link ErrorCode}를 포함하여, 오류 코드에 대한 메시지와 HTTP 상태 코드를 제공합니다.
 * 오류 코드에 대한 상세 정보를 포함하는 예외를 발생시키기 위해 사용됩니다.
 * </p>
 */
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorException 생성자
     *
     * @param errorCode 오류를 나타내는 {@link ErrorCode} 객체
     */
    public ErrorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 오류 메시지를 반환합니다.
     * <p>
     * 오류 메시지는 {@link ErrorCode#getMessage()}에서 제공된 메시지입니다.
     * </p>
     *
     * @return 오류 메시지
     */
    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    /**
     * 오류에 대응하는 HTTP 상태 코드를 반환합니다.
     * <p>
     * HTTP 상태 코드는 {@link ErrorCode#getHttpStatus()}에서 제공된 상태 코드입니다.
     * </p>
     *
     * @return {@link HttpStatus} HTTP 상태 코드
     */
    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
