package webserver.request;

import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// thread safe
// 사용자의 요청을 파싱하여 HttpRequest 객체를 생성
public class HttpRequestParser {
    public static final String REQUEST_LINE_SEPARATOR = " ";
    public static final String HEADER_KEY_SEPARATOR = ":";
    public static final String HEADER_VALUES_SEPARATOR = ";";

    // request input reader로부터 데이터를 읽어들여 HttpRequest 객체를 생성
    public HttpRequest parse(BufferedReader requestInputReader) {
        // Request Body 전 까지 읽어들임
        String beforeBody = readUntilCRLF(requestInputReader);
        // Request Line과 Header Lines 를 분리
        String requestLineString = beforeBody.substring(0, beforeBody.indexOf("\n"));
        String headerLines = beforeBody.substring(beforeBody.indexOf("\n") + 1);

        // Request Line 문자열 파싱
        RequestLine requestLine = parseRequestLine(requestLineString);
        // Header Line 문자열 파싱
        Map<String, String> headers = parseHeaders(headerLines);
        return new HttpRequest(requestLine.method(), requestLine.requestTarget(), requestLine.version(), headers, requestInputReader);
    }

    // 빈 줄이 나올 때 까지 읽기
    private String readUntilCRLF(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        String buffer;
        try {
            while (!(buffer = reader.readLine()).isBlank()) {
                sb.append(buffer);
                sb.append('\n');
            }
            return sb.toString();
        } catch (IOException | NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private RequestLine parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR);
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid Request Line: " + requestLine);
        }
        HttpMethod method = HttpMethod.of(tokens[0]);
        String requestTarget = tokens[1];
        HttpVersion version = HttpVersion.of(tokens[2]);
        return new RequestLine(method, requestTarget, version);
    }

    private Map<String, String> parseHeaders(String headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : headerLines.split("\n")) {
            String[] tokens = line.split(HEADER_KEY_SEPARATOR);
            headers.put(tokens[0], tokens[1]);
        }
        return headers;
    }

    private record RequestLine(HttpMethod method, String requestTarget, HttpVersion version) {

    }
}
