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
    private final String body;
    private final String sid;

    public HttpRequestInfo(InputStream inputStream) throws IOException {
        this.headers = new HashMap<>();
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
        this.body = parseRequestBody(reader);
        this.sid = extractSidFromHeaders();
    }

    private String parseRequestBody(BufferedReader reader) throws IOException {
        String line;
        boolean isBody = false;
        int contentLength = 0;
        StringBuilder body = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) { // 헤더와 본문 사이의 빈 줄
                isBody = true;
                break;
            }
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            } else if (line.toLowerCase().startsWith("cookie:")) {
                headers.put("Cookie", line.substring(8).trim());
            } else {
                String[] headerParts = line.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
            }
        }

        if (isBody && contentLength > 0) {
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            if (read > 0) {
                body.append(buffer, 0, read);
            }
            logger.debug("Body = {}", body);
        }
        return body.toString();
    }

    private String extractSidFromHeaders() {
        if (headers.containsKey("Cookie")) {
            String[] cookies = headers.get("Cookie").split("; ");
            for (String cookie : cookies) {
                String[] keyValue = cookie.split("=");
                if (keyValue.length == 2 && keyValue[0].trim().equals("sid")) {
                    return keyValue[1].trim();
                }
            }
        }
        return null;
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

    public String getSid() {
        return sid;
    }

}
