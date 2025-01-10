package webserver.enums;

import webserver.exception.BadRequest;
import webserver.exception.InternalServerError;

public enum HttpStatusCode {
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    public final Integer statusCode;
    public final String message;

    HttpStatusCode(int code, String message) {
        this.statusCode = code;
        this.message = message;
    }

    public static HttpStatusCode of(int statusCode) {
        if (!(statusCode >= 100 && statusCode < 600)) {
            throw new BadRequest("존재하지 않는 상태 코드입니다.");
        }
        for (HttpStatusCode httpStatusCode : HttpStatusCode.values()) {
            if (httpStatusCode.statusCode == statusCode) {
                return httpStatusCode;
            }
        }
        // 미구현 상태 코드는 서버 잘못이므로 InternalServerError 예외를 발생.
        throw new InternalServerError("미구현 상태 코드입니다.");
    }

}
