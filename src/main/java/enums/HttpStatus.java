package enums;

/**
 * HTTP 상태 코드를 나타내는 열거형 클래스입니다.
 * 각 상태 코드와 해당 상태 코드의 메시지를 정의합니다.
 */
public enum HttpStatus {
    /**
     * 200 OK: 요청이 성공적으로 처리됨
     */
    OK(200, "Ok"),

    /**
     * 201 Created: 요청이 성공적으로 처리되어 리소스가 생성됨
     */
    CREATED(201, "Created"),

    /**
     * 302 Found: 요청한 리소스가 다른 위치에 존재함
     */
    FOUND(302, "Found"),

    /**
     * 303 See Other: 요청에 대한 응답은 다른 URI에서 확인해야 함
     */
    SEE_OTHER(303, "See Other"),

    /**
     * 400 Bad Request: 잘못된 요청, 서버가 요청을 이해할 수 없음
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 401 Unauthorized: 인증이 필요함
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * 404 Not Found: 요청한 리소스를 찾을 수 없음
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * 405 Method Not Allowed: 지원되지 않는 HTTP 메서드 사용
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /**
     * 415 Unsupported Media Type: 지원되지 않는 미디어 타입
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    /**
     * 500 Internal Server Error: 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /**
     * 505 HTTP Version Not Supported: 요청한 HTTP 버전을 지원하지 않음
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int code;
    private final String message;

    /**
     * HttpStatus 생성자
     *
     * @param code 상태 코드
     * @param message 상태 메시지
     */
    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 상태 코드 값을 반환합니다.
     *
     * @return 상태 코드
     */
    public int getCode() {
        return code;
    }

    /**
     * 상태 메시지를 반환합니다.
     *
     * @return 상태 메시지
     */
    public String getMessage() {
        return message;
    }
}
