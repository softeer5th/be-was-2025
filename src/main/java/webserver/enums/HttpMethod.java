package webserver.enums;


import webserver.exception.NotImplemented;

/**
 * HTTP Method을 나타내는 enum
 */
public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;

    /**
     * HTTP Method 이름을 이용해 해당 enum을 반환
     *
     * @param method HTTP Method 이름
     * @return HTTP Method enum
     * @throws NotImplemented 지원하지 않는 HTTP Method일 경우
     */
    public static HttpMethod of(String method) {
        try {
            // Method 이름은 case-sensitive (rfc9110#section-9.1)
            return HttpMethod.valueOf(method);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new NotImplemented("Unsupported HTTP Method", e);
        }
    }
}
