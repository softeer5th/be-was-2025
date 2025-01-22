package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.INVALID_HTTP_METHOD;

/**
 * HTTP 메서드(GET, POST)를 나타내는 열거형 클래스입니다.
 */
public enum HttpMethod {
    /**
     * HTTP GET 메서드
     */
    GET,

    /**
     * HTTP POST 메서드
     */
    POST;

    /**
     * 전달된 문자열이 HTTP 메서드와 일치하는지 확인하고, 일치하지 않으면 예외를 발생시킵니다.
     *
     * @param method 확인할 HTTP 메서드 이름
     * @return 매칭된 {@link HttpMethod}
     * @throws ClientErrorException 유효하지 않은 HTTP 메서드가 전달된 경우 발생
     */
    public static HttpMethod matchOrElseThrow(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ClientErrorException(INVALID_HTTP_METHOD);
        }
    }

    /**
     * 전달된 HTTP 메서드가 POST인지 확인하고, 그렇지 않으면 예외를 발생시킵니다.
     *
     * @param httpMethod 확인할 {@link HttpMethod}
     * @throws ClientErrorException HTTP 메서드가 POST가 아닌 경우 발생
     */
    public static void validPostMethod(HttpMethod httpMethod) {
        if (httpMethod != POST) {
            throw new ClientErrorException(INVALID_HTTP_METHOD);
        }
    }
}
