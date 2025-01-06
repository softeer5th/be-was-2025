package webserver;

import model.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public RequestData parse(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String requestLine = parseRequestLine(br);

        String headers = parseHeaders(br);

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

    private String parseHeaders(BufferedReader br) throws IOException {
        StringBuilder headers = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            headers.append(line).append("\n");
        }
        logger.debug("HTTP Request Header:\n{}", headers);
        return headers.toString();
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

    private RequestData createRequestData(String requestLine, String headers, String body) throws IOException {
        String[] firstLineTokens = requestLine.split(" ");
        if (firstLineTokens.length < 2) {
            throw new IOException("Invalid HTTP request: Malformed request line");
        }

        String method = firstLineTokens[0];
        String path = firstLineTokens[1];
        return new RequestData(method, path, body);
    }
}