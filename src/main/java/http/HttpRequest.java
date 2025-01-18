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

    public HttpRequest(List<String> headerLines, BufferedReader br, Logger logger) throws IOException {
        headers = new HashMap<>();
        log(logger);
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

    public String getCookieSid() {
        try {
            String s = headers.get(HttpHeader.COOKIE.getHeaderName());
            String[] cookies = s.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("sid=")) {
                    String sid = cookie.split("=")[1];
                    return sid;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
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
            headers.put(tokens[0].toLowerCase(), tokens[1]);
        }
    }

    public String getBody() {
        return body;
    }

    public int getContentLength() {
        if (headers.containsKey(HttpHeader.CONTENT_LENGTH.getHeaderName())) {
            return Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.getHeaderName()).trim());
        }
        throw new ExceptionInInitializerError("Can't Find content-length");
    }

    public void log(Logger logger) {
        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append("\nHeader : {\n");
        logMessageBuilder.append("method : " + httpMethod).append(' ').append("path : " + requestPath).append('\n');
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
