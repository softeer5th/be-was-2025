package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    private HttpRequestParser() {}

    public static HttpRequest parseRequest(InputStream in) throws IOException, URISyntaxException {
        BufferedInputStream inputStream = new BufferedInputStream(in);
        StringBuilder requestBuilder = new StringBuilder();
        String requestHeaders;
        byte[] requestBody = null;

        // HTTP Request Start Line
        String startLine = readLine(inputStream);
        logger.debug("Start Line: {}", startLine);

        HttpMethod method;
        TargetInfo targetInfo;
        HttpVersion version;
        String[] tokens = startLine.split(" ");

        if (tokens.length == 3) {
            method = HttpMethod.getMethodFromString(tokens[0]);
            targetInfo = new TargetInfo(tokens[1]);
            version = HttpVersion.getVersionFromString(tokens[2]);
        } else {
            method = HttpMethod.INVALID;
            targetInfo = null;
            version = HttpVersion.INVALID;
            return new HttpRequest(method, targetInfo, version, null, null);
        }

        // HTTP Request Headers
        String line;
        while (!(line = readLine(inputStream)).isEmpty()) {
            logger.debug("Header Line: {}", line);
            requestBuilder.append(line).append("\n");
        }
        requestHeaders = requestBuilder.toString();
        logger.debug("\nHTTP Request Header: \n{}\n", requestHeaders);

        int contentLength = getContentLength(requestHeaders);
        if (contentLength > 0) {
            requestBody = getRequestBody(inputStream, contentLength);
//            logger.debug("\nHTTP Request Body: \n{}\n", new String(requestBody, "UTF-8"));
        }

        return new HttpRequest(method, targetInfo, version, parseRequestHeaders(requestHeaders), requestBody);
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        while ((read = in.read()) != -1) {
            if (read == '\r') {
                int next = in.read();
                if (next == '\n') {
                    break;
                }
            }
            buffer.write(read);
        }
        return buffer.toString("UTF-8");
    }

    public static Map<String, String> parseRequestHeaders(String requestHeaders) {
        Map<String, String> requestHeadersMap = new HashMap<>();
        if (requestHeaders != null) {
            String[] tokens = requestHeaders.split("\n");
            for (String token : tokens) {
                String[] keyValue = token.split(":", 2);
                if (keyValue.length == 2) {
                    requestHeadersMap.put(keyValue[0].toLowerCase().trim(), keyValue[1].trim());
                }
            }
        }
        return requestHeadersMap;
    }

    public static Map<String, Object> parseRequestBody(String requestBody) throws UnsupportedEncodingException {
        Map<String, Object> requestBodyMap = new HashMap<>();
        if (requestBody != null) {
            for (String key : requestBody.split("&")) {
                String[] keyValue = key.split("=");
                if (keyValue.length == 2) {
                    requestBodyMap.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }
        }
        return requestBodyMap;
    }

    private static byte[] getRequestBody(InputStream inputStream, int contentLength) throws IOException {
        logger.debug("Content Length: {}", contentLength);
        byte[] buffer = new byte[contentLength];
        int bytesRead = 0;
        while (contentLength - bytesRead > 0) {
            bytesRead += inputStream.read(buffer, bytesRead, contentLength - bytesRead);
        }
        logger.debug("Read {} bytes", bytesRead);
        return buffer;
    }

    private static int getContentLength(String requestHeaders) {
        for (String line : requestHeaders.split("\n")) {
            if (line.toLowerCase().contains("content-length")) {
                return Integer.parseInt(line.split(":")[1].trim());
            }
        }
        return 0;
    }
}