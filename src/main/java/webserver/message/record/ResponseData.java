package webserver.message.record;

import webserver.enumeration.HTTPStatusCode;

import java.util.HashMap;
import java.util.Map;

public class ResponseData<T> {
    T data;
    Map<String, String> headers = new HashMap<>();
    HTTPStatusCode status;

    public static <T> ResponseData<T> ok(T data) {
        ResponseData<T> entity = new ResponseData<>();
        entity.data = data;
        entity.status = HTTPStatusCode.OK;
        return entity;
    }

    public static ResponseData<String> redirect(String location) {
        ResponseData<String> entity = new ResponseData<>();
        entity.data = null;
        entity.status = HTTPStatusCode.FOUND;
        entity.headers.put("Location", location);
        return entity;
    }

    public HTTPStatusCode getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
