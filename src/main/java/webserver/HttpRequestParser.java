package webserver;

import global.model.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public HttpRequest parse(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String requestLine = parseRequestLine(br);

        Map<String, String> headers = parseHeaders(br);

        String body = parseBody(br);

        return createRequestData(requestLine, headers, body);
    }

    private String parseRequestLine(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid HTTP request: Empty request line");
        }
        logger.debug("Request Line: {}", requestLine);
        return requestLine;
    }

    private Map<String, String> parseHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new ConcurrentHashMap<>();

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int index = line.indexOf(":");
            if (index == -1) {
                continue;
            }
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            headers.put(key, value);
        }

        logger.debug("HTTP Request Headers: {}", headers);
        return headers;
    }

    private String parseBody(BufferedReader br) throws IOException {
        StringBuilder body = new StringBuilder();
        while (br.ready()) {
            body.append((char) br.read());
        }
        if (!body.isEmpty()) {
            logger.debug("HTTP Request Body:\n{}", body);
        }
        return body.toString();
    }

    private HttpRequest createRequestData(String requestLine, Map<String, String> headers, String body) throws IOException {
        String[] firstLineTokens = requestLine.split("\\s+");
        if (firstLineTokens.length < 3) {
            throw new IOException("Invalid HTTP request: Malformed request line");
        }

        String method = firstLineTokens[0];
        String path = firstLineTokens[1];
        return new HttpRequest(method, path, headers, body);
    }
}