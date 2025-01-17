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

    private HttpRequestParser() {
    }

    public static HttpRequest parseRequest(InputStream in) throws IOException, URISyntaxException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8")); // InputStream => InputStreamReader => BufferedReader
        StringBuilder requestBuilder = new StringBuilder();
        String requestHeaders;
        String requestBody = null;

        // HTTP Request Start Line
        String startLine = reader.readLine();
        logger.debug(startLine);

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
        logger.debug("Start Line: " + method + " " + targetInfo + " " + version);

        // HTTP Request Headers
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            logger.debug(line);
            requestBuilder.append(line).append("\n");
        }
        requestHeaders = requestBuilder.toString();
        logger.debug("\nHTTP Request Header: \n" + requestHeaders + "\n");

        int contentLength = getContentLength(requestHeaders);
        if (contentLength > 0) {
            requestBody = getRequestBody(reader, contentLength);
            logger.debug("\nHTTP Request Body: \n" + requestBody + "\n");
        }

        return new HttpRequest(method, targetInfo, version, parseRequestHeaders(requestHeaders), requestBody);
    }

    public static Map<String, String> parseRequestHeaders(String requestHeaders) {
        Map<String, String> requestHeadersMap = new HashMap<>();
        if (requestHeaders != null) {
            requestHeaders = requestHeaders.trim();
            String[] tokens = requestHeaders.split("\n");
            for (String token : tokens) {
                String[] keyValue = token.split(":");
                requestHeadersMap.put(keyValue[0], keyValue[1].trim());
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

    private static String getRequestBody(BufferedReader reader, int contentLength) throws IOException {
        // HTTP Request Body
        char[] buffer = new char[contentLength];
        int bytesRead = reader.read(buffer, 0, contentLength);
        return new String(buffer, 0, bytesRead);
    }

    private static int getContentLength(String requestHeaders) {
        for (String line : requestHeaders.split("\n")) {
            if (line.contains("Content-Length")) {
                return Integer.parseInt(line.split(":")[1].trim());
            }
        }
        return 0;
    }
}
