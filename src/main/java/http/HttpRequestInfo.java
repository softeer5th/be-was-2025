package http;

import exception.BaseException;
import exception.HttpErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestInfo {
    private final static Logger logger = LoggerFactory.getLogger(HttpRequestInfo.class);

    private final HttpMethod method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, Cookie> cookies;
    private final String body;

    public HttpRequestInfo(InputStream inputStream) throws IOException {
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            logger.error("Request line is empty");
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        String[] requestTokens = requestLine.replaceAll("\\s+", " ").trim().split(" ");
        if (requestTokens.length != 3) {
            logger.error("Request token length is not 3");
            throw new BaseException(HttpErrorCode.INVALID_HTTP_REQUEST);
        }

        this.method = HttpMethod.match(requestTokens[0].toLowerCase());
        this.path = requestTokens[1];
        parseHeaders(reader);
        this.body = parseRequestBody(reader);
    }

    private void parseHeaders(BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("cookie:")) {
                this.cookies.putAll(parseCookies(line.substring(8).trim()));
            } else {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim().toLowerCase(), headerParts[1].trim().toLowerCase());
                }
            }
        }
    }

    private String parseRequestBody(BufferedReader reader) throws IOException {
        int contentLength = 0;
        StringBuilder body = new StringBuilder();

        if (headers.containsKey("Content-Length".toLowerCase())) {
            contentLength = Integer.parseInt(headers.get("Content-Length".toLowerCase()));
        }

        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            if (read > 0) {
                body.append(buffer, 0, read);
            }
            logger.debug("Body = {}", body);
        }

        return body.toString();
    }

    private Map<String, Cookie> parseCookies(String cookieHeader) {
        String[] cookieParts = cookieHeader.split("; ");
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie newCookie = null;


        for (String part : cookieParts) {
            if (part.contains("=")) {
                String[] keyValue = part.split("=", 2);
                String name = keyValue[0].trim();
                String value = keyValue[1].trim();

                newCookie = new Cookie(name, value);
                cookies.put(name, newCookie);
            } else {
                if (newCookie != null) {
                    applyCookieOption(newCookie, part.trim(), "");
                }
            }
        }
        return cookieMap;
    }


    private void applyCookieOption(Cookie cookie, String option, String value) {
        switch (option.toLowerCase()) {
            case "max-age":
                cookie.setMaxAge(Long.parseLong(value));
                break;
            case "path":
                cookie.setPath(value);
                break;
            case "httponly":
                cookie.setHttpOnly(true);
                break;
            default:
                break;
        }
    }


    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public Cookie getCookie(String name) {
        return cookies.get(name);
    }
}
