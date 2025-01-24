package webserver.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.HttpRequest;
import webserver.http.cookie.Cookie;
import webserver.session.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        return parseRequest(inputStream);
    }

    private static HttpRequest parseRequest(InputStream inputStream) throws IOException {
        HttpRequest request = new HttpRequest();

        String rawRequestHeader = readRawRequestHeader(inputStream);

        String requestHeader = extractHeaders(rawRequestHeader);
        parseHeaders(requestHeader, request);

        String contentLengthHeader = request.getHeader("content-length");
        if (contentLengthHeader != null) {
            parseBody(rawRequestHeader, inputStream, request, Integer.parseInt(contentLengthHeader));
        }

        return request;
    }

    private static String readRawRequestHeader(InputStream inputStream) throws IOException {
        StringBuilder headersBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int bytesRead;
        boolean isHeaderEnd = false;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            String chunk = new String(buffer, 0, bytesRead, StandardCharsets.ISO_8859_1);
            headersBuilder.append(chunk);

            if (headersBuilder.indexOf("\r\n\r\n") != -1) {
                isHeaderEnd = true;
                break;
            }
        }

        if (!isHeaderEnd) {
            throw new IOException("Invalid HTTP request: Headers not terminated with \\r\\n\\r\\n.");
        }

        logger.debug("Raw HTTP Request:\n{}", headersBuilder);
        return headersBuilder.toString();
    }

    private static String extractHeaders(String rawRequest) {
        int headerEndIndex = rawRequest.indexOf("\r\n\r\n");
        return rawRequest.substring(0, headerEndIndex);
    }

    private static void parseHeaders(String headerPart, HttpRequest request) {
        String[] lines = headerPart.split("\r\n");

        String[] requestLine = lines[0].split(" ");
        if (requestLine.length != 3) {
            throw new IllegalArgumentException("Invalid request line: " + lines[0]);
        }

        String method = requestLine[0].trim();
        String uri = requestLine[1].trim();
        String[] uriElements = uri.split("\\?");
        String version = requestLine[2].trim();

        request.setMethod(method);
        request.setPath(uriElements[0]);
        request.setVersion(version);

        if(uriElements.length == 2) {
            parseUrlParameters(uriElements[1].trim(), request);
        }
        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(":", 2);
            if (header.length == 2) {
                if(header[0].trim().equalsIgnoreCase("cookie")) parseCookie(request, header[1].trim());

                request.setHeader(header[0].trim().toLowerCase(), header[1].trim());
            }
        }

        logger.debug("Parsed Headers:\n{}", headerPart);
    }

    private static void parseCookie(HttpRequest request, String cookieString) {
        String[] cookies = cookieString.split(";");
        for (String cookie : cookies) {
            String[] cookieParts = cookie.split("=");
            if (cookieParts.length == 2) {
                if(cookieParts[0].trim().equalsIgnoreCase(HttpSession.SESSION_NAME)) request.setSessionId(cookieParts[1].trim());
                Cookie requestedCookie = new Cookie(cookieParts[0].trim(), cookieParts[1].trim());
                request.addCookie(requestedCookie);
            }
        }
    }

    private static void parseBody(String rawRequest, InputStream inputStream, HttpRequest request, int contentLength) throws IOException {
        int headerEndIndex = rawRequest.indexOf("\r\n\r\n");
        byte[] initialBodyData = rawRequest.substring(headerEndIndex + 4).getBytes(StandardCharsets.ISO_8859_1);

        byte[] bodyBuffer = new byte[contentLength];
        int remainingLength = Math.min(contentLength, initialBodyData.length);
        System.arraycopy(initialBodyData, 0, bodyBuffer, 0, remainingLength);

        if (remainingLength < contentLength) {
            int additionalBytes = inputStream.read(bodyBuffer, remainingLength, contentLength - remainingLength);
            logger.debug("Additional bytes read from InputStream: {}", additionalBytes);
        }

        request.setBody(bodyBuffer);

        String contentType = request.getHeader("content-type");

        if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {

            logger.debug("Parsed Body as Bytes: {}", bodyBuffer);
            parseUrlParameters(new String(bodyBuffer, StandardCharsets.UTF_8), request);
        }
    }

    private static void parseUrlParameters(String url, HttpRequest request) {
        String[] pairs = url.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                request.setParameter(key, value);
            }
        }

        logger.debug("Parsed URL-encoded Body:\n{}", url);
    }
}