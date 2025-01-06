package webserver;

import com.sun.net.httpserver.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpRequest {
    private HttpMethod method;
    private String uri;
    private final Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

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

    public String getBody() {
        return body;
    }

    private void parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("Method not supported");
        }
        String[] start = requestLine.trim().split(" ");
        if (start.length != 3) {
            throw new IOException("Method not supported");
        }
        this.method = HttpMethod.valueOf(start[0]);
        String[] uriParts = start[1].split("\\?");
        this.uri = uriParts[0].trim();
        if (uriParts.length > 1) {
            parseQueryParameter(uriParts[1].trim());
        }
        this.protocol = start[2];
    }

    private void parseQueryParameter(String rawQueryParams) throws IOException {
        String[] queryParams = rawQueryParams.split("&");
        for (String queryParam : queryParams) {
            String[] paramPair = queryParam.split("=");
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
            String[] parts = line.split(": ");
            String key = parts[0].trim();
            String value = parts[1].trim();
            logger.debug("{}: {}", key, value);
            headers.put(key, value);
        }
    }
}

