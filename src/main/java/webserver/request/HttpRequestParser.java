package webserver.request;

import webserver.config.ServerConfig;
import webserver.enums.HttpMethod;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.enums.ParsingConstant;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.header.RequestHeader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static webserver.enums.HttpHeader.COOKIE;
import static webserver.enums.HttpHeader.HOST;
import static webserver.enums.ParsingConstant.*;

// 사용자의 요청을 파싱하여 HttpRequest 객체를 생성
public class HttpRequestParser {
    private final int MAX_HEADER_SIZE;

    public HttpRequestParser(ServerConfig config) {
        this.MAX_HEADER_SIZE = config.getMaxHeaderSize();
    }

    // Body 직전 헤더까지 읽기
    public String readUntilBody(InputStream inputStream) throws IOException {
        int totalReadBytes = 0;
        StringBuilder sb = new StringBuilder();
        int buf;
        // request line 앞에 오는 연속된 CRLF는 무시해야 함. (rfc9112#section-2.2)
        while ((buf = inputStream.read()) != -1) {
            if (++totalReadBytes > MAX_HEADER_SIZE)
                throw new HttpException(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "Header Size가 너무 큽니다.");
            sb.append((char) buf);
            if (sb.length() >= 2) {
                if (CRLF.equals(sb.toString())) {
                    sb.delete(0, sb.length());
                    continue;
                }
                break;

            }
        }

        // request line ~ header까지 읽기
        while ((buf = inputStream.read()) != -1) {
            if (++totalReadBytes > MAX_HEADER_SIZE)
                throw new HttpException(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "Header Size가 너무 큽니다.");
            sb.append((char) buf);
            // \n 혹은 \r\n이 2번 나오면 header가 끝난 것으로 간주
            if (sb.length() >= 4 && sb.substring(sb.length() - 4).matches("(" + HTTP_HEADERS_END_DELIMITER.value + ")$")) {
                break;
            }
        }
        return sb.toString();
    }

    // request input reader로부터 데이터를 읽어들여 HttpRequest 객체를 생성
    public HttpRequest parse(InputStream in) {
        try {
            // request-line + headers 문자열
            String headerSection = readUntilBody(in);

            // Request Line과 Header Lines 를 분리
            String[] tokens = headerSection.split(HTTP_LINE_SEPARATOR.value, 2);
            String requestLineString = tokens[0];
            String headerLines = tokens[1];

            if (!requestLineString.strip().equals(requestLineString))
                throw new BadRequest("Request Line 앞뒤로 공백이 있습니다.");

            // Request Line 문자열 파싱
            RequestLine requestLine = parseRequestLine(requestLineString);
            // Header Line 문자열 파싱
            RequestHeader headers = parseHeaders(headerLines);

            RequestBody bodyParser = new RequestBody(in, headers);

            return new HttpRequest(requestLine.method(), requestLine.requestTarget(), requestLine.version(), headers, bodyParser);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequest("Invalid Request", e);
        }
    }

    private record RequestLine(HttpMethod method, RequestTarget requestTarget, HttpVersion version) {

    }

    // Request Line 문자열을 파싱하여 RequestLine 객체 생성
    private RequestLine parseRequestLine(String requestLine) {
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR.value);
        if (tokens.length != 3) {
            throw new BadRequest("Invalid Request Line: " + requestLine);
        }
        HttpMethod method = HttpMethod.of(tokens[0]);
        RequestTarget requestTarget = parseRequestTarget(tokens[1]);
        HttpVersion version = HttpVersion.of(tokens[2]);
        return new RequestLine(method, requestTarget, version);
    }

    // Header Line 문자열을 파싱하여 Map<String, String> 객체 생성
    private RequestHeader parseHeaders(String headerLines) {
        RequestHeader headers = new RequestHeader();
        for (String line : headerLines.split(HTTP_LINE_SEPARATOR.value)) {
            String[] tokens = line.split(HEADER_KEY_SEPARATOR.value, 2);
            // header name과 : 사이에 공백이 있으면 400 응답해야 함. (rfc9112#section-5.1)
            String headerName = tokens[0];
            String headerValue = tokens[1].strip();
            if (headerName.endsWith(SP.value))
                throw new BadRequest("Header name 뒤에 공백이 있습니다.");
            // 서버는 클라이언트가 Host 헤더를 여러번 보내면 400 응답해야 함. (rfc9112#section-3.2)
            if (HOST.equals(headerName) && headers.containsHeader(HOST))
                throw new BadRequest("Host Header가 중복되었습니다.");
            if (COOKIE.equals(headerName))
                headers.addCookies(parseCookies(headerValue));
            else
                headers.setHeader(headerName, headerValue);
        }
        // 서버는 클라이언트가 Host 헤더를 보내지 않으면 400 응답해야 함. (rfc9112#section-3.2)
        if (!headers.containsHeader(HOST))
            throw new BadRequest("Host Header가 없습니다.");
        return headers;
    }

    // 쿠키 정보를 파싱하여 Map으로 반환
    private Map<String, String> parseCookies(String value) {
        Map<String, String> cookies = new HashMap<>();
        String[] cookiePairs = value.split(ParsingConstant.COOKIE_SEPARATOR.value);
        for (String cookiePair : cookiePairs) {
            String[] cookie = cookiePair.split(ParsingConstant.COOKIE_KEY_VALUE_SEPARATOR.value);
            cookies.put(cookie[0].trim(), cookie[1].trim());
        }
        return cookies;
    }

    // Request Target 문자열을 파싱하여 RequestTarget 객체 생성
    private RequestTarget parseRequestTarget(String requestTarget) {
        URI uri = URI.create(requestTarget);
        String path = uri.getPath();
        String query = uri.getQuery();
        Map<String, String> queryMap = new HashMap<>();
        if (query != null && !query.isBlank()) {
            String[] paramTokens = query.split(QUERY_PARAMETER_SEPARATOR.value);
            for (String paramToken : paramTokens) {
                if (!paramToken.contains(QUERY_KEY_VALUE_SEPARATOR.value))
                    throw new BadRequest("Invalid Query Parameter: " + paramToken);

                String[] keyValue = paramToken.split(QUERY_KEY_VALUE_SEPARATOR.value, 2);
                if (keyValue.length == 1)
                    queryMap.put(keyValue[0].strip(), QUERY_DEFAULT_VALUE.value);
                else
                    queryMap.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
        return new RequestTarget(path, queryMap);
    }
}
