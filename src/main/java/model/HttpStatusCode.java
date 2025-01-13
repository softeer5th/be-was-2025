package model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum HttpStatusCode {
    CONTINUE(100, "Continue", Collections.emptyList()),
    SWITCHING_PROTOCOLS(101, "Switching Protocols", Collections.emptyList()),
    PROCESSING(102, "Processing", Collections.emptyList()),
    OK(200, "OK", Arrays.asList("Content-Type", "Content-Length")),
    CREATED(201, "Created", Collections.emptyList()),
    ACCEPTED(202, "Accepted", Collections.emptyList()),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information", Collections.emptyList()),
    SEE_OTHER(303, "See Other", Collections.emptyList()),
    BAD_REQUEST(400, "Bad Request", Collections.emptyList()),
    NOT_FOUND(404, "Not Found", Collections.emptyList()),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", Collections.emptyList()),
    NOT_IMPLEMENTED(501, "Not Implemented", Collections.emptyList()),
    BAD_GATEWAY(502, "Bad Gateway", Collections.emptyList());

    private final int code;
    private final String description;
    private final List<String> headers;

    HttpStatusCode(int code, String description, List<String> headers) {
        this.code = code;
        this.description = description;
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getStartLine() {
        return "HTTP/1.1 " + code + " " + description + "\r\n";
    }
}

