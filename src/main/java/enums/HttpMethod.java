package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.INVALID_HTTP_METHOD;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod matchOrElseThrow(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ClientErrorException(INVALID_HTTP_METHOD);
        }
    }

    public static void validPostMethod(HttpMethod httpMethod){
        if(httpMethod != POST)
            throw  new ClientErrorException(INVALID_HTTP_METHOD);
    }
}
