package webserver.request;

import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.BadRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

// 사용자의 요청을 파싱하여 HttpRequest 객체를 생성
public class HttpRequestParser {
    public static final String HTTP_LINE_SEPARATOR = "\\r?\\n";
    public static final String REQUEST_LINE_SEPARATOR = " ";
    public static final String HEADER_KEY_SEPARATOR = ":";
    public static final String HEADER_VALUES_SEPARATOR = ";";
    public static final String QUERY_PARAMETER_SEPARATOR = "&";
    public static final String QUERY_KEY_VALUE_SEPARATOR = "=";

    // request input reader로부터 데이터를 읽어들여 HttpRequest 객체를 생성
    public HttpRequest parse(BufferedReader requestInputReader) {
        try {
            // Request Body 전 까지 읽어들임
            String beforeBody = readUntilCRLF(requestInputReader);
            // Request Line과 Header Lines 를 분리
            String[] tokens = beforeBody.split(HTTP_LINE_SEPARATOR, 2);
            String requestLineString = tokens[0].strip();
            String headerLines = tokens[1].strip();

            // Request Line 문자열 파싱
            RequestLine requestLine = parseRequestLine(requestLineString);
            // Header Line 문자열 파싱
            Map<String, String> headers = parseHeaders(headerLines);
            return new HttpRequest(requestLine.method(), requestLine.requestTarget(), requestLine.version(), headers, requestInputReader);
        } catch (Exception e) {
            throw new BadRequest("Invalid Request", e);
        }
    }

    // 빈 줄이 나올 때 까지 읽기
    private String readUntilCRLF(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String buffer;
        while (true) {
            buffer = reader.readLine();
            if (buffer == null || buffer.isBlank()) {
                break;
            }
            sb.append(buffer);
            sb.append('\r');
            sb.append('\n');
        }
        return sb.toString();
    }

    // Request Line 문자열을 파싱하여 RequestLine 객체 생성
    private RequestLine parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR);
        if (tokens.length != 3) {
            throw new BadRequest("Invalid Request Line: " + requestLine);
        }
        HttpMethod method = HttpMethod.of(tokens[0]);
        RequestTarget requestTarget = parseRequestTarget(tokens[1]);
        HttpVersion version = HttpVersion.of(tokens[2]);
        return new RequestLine(method, requestTarget, version);
    }

    // Header Line 문자열을 파싱하여 Map<String, String> 객체 생성
    private Map<String, String> parseHeaders(String headerLines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : headerLines.split(HTTP_LINE_SEPARATOR)) {
            String[] tokens = line.split(HEADER_KEY_SEPARATOR, 2);
            headers.put(tokens[0].strip(), tokens[1].strip());
        }
        return headers;
    }

    // Request Target 문자열을 파싱하여 RequestTarget 객체 생성
    private RequestTarget parseRequestTarget(String requestTarget) {
        URI uri = URI.create(requestTarget);
        String path = uri.getPath();
        String query = uri.getQuery();
        Map<String, String> queryMap = new HashMap<>();
        if (query != null && !query.isBlank()) {
            String[] paramTokens = query.split(QUERY_PARAMETER_SEPARATOR);
            for (String paramToken : paramTokens) {
                String[] keyValue = paramToken.split(QUERY_KEY_VALUE_SEPARATOR, 2);
                if (keyValue.length != 2) {
                    throw new BadRequest("Invalid Query Parameter: " + paramToken);
                }
                // key가 중복되는 경우 앞선 값을 덮어씀
                queryMap.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
        return new RequestTarget(path, queryMap);
    }

    private record RequestLine(HttpMethod method, RequestTarget requestTarget, HttpVersion version) {

    }
}
