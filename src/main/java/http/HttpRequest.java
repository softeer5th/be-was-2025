package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

public class HttpRequest {
    private HttpMethod httpMethod;
    private Map<String, String> headers;
    private String body;
    private String requestPath;

    public HttpRequest(List<String> headerLines, BufferedReader br) throws IOException {
        headers = new HashMap<>();
        setHeader(headerLines);
        setBody(br);
    }

    private void setBody(BufferedReader br) throws IOException {
        if (this.httpMethod != HttpMethod.POST) return;
        int contentLength = this.getContentLength();
        char[] requestBody = new char[contentLength];
        br.read(requestBody, 0, contentLength);
        this.body = new String(requestBody);
    }

    public String getRequestPath() {
        return requestPath;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    private void setHeader(List<String> headerLines) {
        String[] requestLineTokens = headerLines.get(0).split(" ");
        String method = requestLineTokens[0];
        requestPath = requestLineTokens[1];

        httpMethod = switch (method) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "DELETE" -> HttpMethod.DELETE;
            case "PATCH" -> HttpMethod.PATCH;
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

        for (int i = 1; i < headerLines.size(); i++) {
            String[] tokens = headerLines.get(i).split(":", 2);
            headers.put(tokens[0], tokens[1]);
        }
    }

    public String getBody() {
        return body;
    }

    public int getContentLength() {
        if (headers.containsKey("Content-Length")) {
            return Integer.parseInt(headers.get("Content-Length").trim());
        }
        throw new ExceptionInInitializerError("Can't Find Content-Length");
    }

    public void log(Logger logger) {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append("\nHeader : {\n");
        for (String key : headers.keySet()) {
            logMessageBuilder.append(key).append(": ").append(headers.get(key)).append('\n');
        }
        logMessageBuilder.append("}\n");

        logMessageBuilder.append("\nBody : {\n");
        logMessageBuilder.append(body).append('\n');
        logMessageBuilder.append("}\n");
        logger.debug(logMessageBuilder.toString());
    }
}
