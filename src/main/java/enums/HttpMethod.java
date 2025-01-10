package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.INVALID_HTTP_METHOD;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod matchOrElseThrow(String method) {
        return switch (method) {
            case "get" -> GET;
            case "post" -> POST;
            default -> throw new ClientErrorException(INVALID_HTTP_METHOD);
        };
    }
}
