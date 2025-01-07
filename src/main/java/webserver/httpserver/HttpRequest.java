package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.FileNotSupportedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpRequest {
    public static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    public static final String QUERYPARAMETER_DELIMITER = "&";
    public static final String QUERYPARAMETER_KEVALUE_DELIMITER = "=";
    public static final String URI_QUERYPARAM_DELIMITER = "\\?";
    private HttpMethod method;
    private String uri;
    private final Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(BufferedReader reader) throws IOException {
        parseRequestLine(reader);
        parseHeader(reader);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUri() {
        return uri;
    }

    public byte[] getBody() {
        return body;
    }

    public String guessContentType() {
        String lowerName = uri.toLowerCase();
        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
            return "text/html; charset=utf-8";
        } else if (lowerName.endsWith(".css")) {
            return "text/css; charset=utf-8";
        } else if (lowerName.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".json")) {
            return "application/json";
        } else if (lowerName.endsWith(".svg") || lowerName.endsWith(".xml")) {
            return "image/svg+xml";
        } else if (lowerName.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            // 확장자가 아무것도 매칭되지 않았을 경우, error 페이지를 서빙하기 위해 예외 발생시킴
            throw new FileNotSupportedException();
        }
    }


    private void parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("Method not supported");
        }
        String[] requestLineParts = requestLine.trim().split(" ");
        if (requestLineParts.length != 3) {
            throw new IOException("Method not supported");
        }
        this.method = HttpMethod.valueOf(requestLineParts[0]);
        String[] uriParts = requestLineParts[1].split(URI_QUERYPARAM_DELIMITER);
        this.uri = uriParts[0].trim();
        if (uriParts.length > 1) {
            parseQueryParameter(uriParts[1].trim());
        }
        this.protocol = requestLineParts[2];
    }

    private void parseQueryParameter(String rawQueryParams) throws IOException {
        String[] queryParams = rawQueryParams.split(QUERYPARAMETER_DELIMITER);
        for (String queryParam : queryParams) {
            String[] paramPair = queryParam.split(QUERYPARAMETER_KEVALUE_DELIMITER);
            if (paramPair.length != 2) {
                throw new IOException("Invalid query parameter: " + queryParam);
            }
            String key = URLDecoder.decode(paramPair[0].trim(), UTF_8);
            String value = URLDecoder.decode(paramPair[1].trim(), UTF_8);
            this.parameters.put(key, value);
        }
    }

    private void parseHeader(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) break;
            String[] parts = line.split(HEADER_KEY_VALUE_DELIMITER);
            String key = parts[0].trim();
            String value = parts[1].trim();
            logger.debug("{}: {}", key, value);
            headers.put(key, value);
        }
    }
}

