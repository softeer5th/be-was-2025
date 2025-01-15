package webserver;

import java.util.*;

public class HTTPRequestHeader {
    private final String method;
    private final String uri;
    private final String version;

    Map<String, String> headers;

    // Todo: body 관련 구현 옮기기
    public HTTPRequestHeader(String header) {
        String[] headersString = header.split("\n");
        this.headers = new HashMap<>();

        // requestLine에는 method, uri, HTTP 버전이 들어간다.
        String[] requestLine = headersString[0].split("\\s+");

        try {
            checkValidateRequestLine(requestLine);
        } catch (HTTPExceptions.Error400 e) {
            throw new HTTPExceptions.Error400("400 Bad Request: " + e.getMessage());
        } catch (HTTPExceptions.Error404 e) {
            throw new HTTPExceptions.Error404("404 Not Found: " + e.getMessage());
        } catch (HTTPExceptions.Error405 e) {
            throw new HTTPExceptions.Error405("405 Method Not Allowed: " + e.getMessage());
        } catch (HTTPExceptions.Error505 e) {
            throw new HTTPExceptions.Error505("505 Method Not Allowed: " + e.getMessage());
        }

        this.method = requestLine[0];
        this.uri = requestLine[1];
        this.version = requestLine[2];

        // heasersMap에는 request header가 들어간다.
        for (int line = 1; line < headersString.length; line++) {
            String headerString = headersString[line].trim();

            // 헤더가 빈 줄일 경우 무시
            if (headerString.isEmpty()) {
                continue;
            }

            int colonIndex = headerString.indexOf(":");
            // 헤더에 colon이 없는 경우
            if (colonIndex == -1) {
                // 400 Bad Request
                throw new HTTPExceptions.Error400("400 Bad Request: Invalid colon in header");
            }

            // 헤더의 key, value값 모두 대소문자를 구분하지 않는다.
            // 소문자로 변환
            String key = headerString.substring(0, colonIndex).trim().toLowerCase();
            String value = headerString.substring(colonIndex + 1).trim().toLowerCase();

            // 중복된 키값일 경우 가장 마지막에 들어온 키값을 기준으로 한다.
            this.headers.put(key, value);
        }

        try {
            // header 검증
            checkValidateHeader();
        } catch (HTTPExceptions.Error400 e) {
            throw new HTTPExceptions.Error400("400 Bad Request: " + e.getMessage());
        }
    }

    private void checkValidateRequestLine(String[] requestLine) {
        // 공백 검사
        if (requestLine.length != 3) {
            // 400 Bad Request
            throw new HTTPExceptions.Error400("Invalid request line");
        }
        // method 검사
        if (!HTTPMethod.isValid(requestLine[0])) {
            // 400 Bad Request
            throw new HTTPExceptions.Error400("Invalid HTTP method");
        }
        if (!HTTPMethod.isSupported(requestLine[0])) {
            // 405 Method Not Allowed
            throw new HTTPExceptions.Error405("HTTP method not supported");
        }
        // HTTP version 검사
        if (!HTTPVersion.isValid(requestLine[2])) {
            // 505 HTTP Version Not Supported
            throw new HTTPExceptions.Error505("Invalid HTTP version");
        }
    }

    // HTTP 표준에 근거해 유효한 header인지 검증하는 메서드
    // 유효하지 않은 요청일 경우 400 Bad Request
    private void checkValidateHeader() {
        // Todo: header의 value값이 유효한 값인지 검증 (미구현)
        // 필수 헤더 검증 - host
        if (!this.headers.containsKey("host")) {
            // 400 Bad Request
            throw new HTTPExceptions.Error400("request header missing key 'Host'");
        }
        // Todo: 입력받은 헤더의 키값이 올바른지 검증 (미구현)
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}