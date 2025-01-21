package webserver.httpserver;

public enum HttpMethod {
    GET, POST;

    public static boolean isSupported(String method){
        for (HttpMethod httpMethod : values()) {
            if (method.equals(httpMethod.toString()))
                return true;
        }
        return false;
    }
}
