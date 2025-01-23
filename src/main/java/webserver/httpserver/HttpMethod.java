package webserver.httpserver;

public enum HttpMethod {
    GET, POST;

    /**
     * 해당 문자열이 지원하는 HTTP 메소드에 포함되는지 확인하는 메소드
     * @param method
     * @return
     */
    public static boolean isSupported(String method){
        for (HttpMethod httpMethod : values()) {
            if (method.equals(httpMethod.toString()))
                return true;
        }
        return false;
    }
}
