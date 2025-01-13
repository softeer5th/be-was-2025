package webserver.request;

import webserver.common.HttpHeaders;
import webserver.enums.HttpMethod;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.exception.InternalServerError;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static webserver.enums.HttpHeader.CONTENT_LENGTH;
import static webserver.enums.ParsingConstant.*;

// 사용자의 요청을 파싱하여 HttpRequest 객체를 생성
public class HttpRequestParser {
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^((" + HTTP_LINE_SEPARATOR.value + ")*)");
    private static final Pattern END_OF_HEADER_PATTERN = Pattern.compile(HTTP_HEADERS_END_DELIMITER.value);

    // request input reader로부터 데이터를 읽어들여 HttpRequest 객체를 생성
    public HttpRequest parse(InputStream in) {
        try {
            byte[] requestBytes = in.readNBytes(MAX_HEADER_SIZE);
            String requestString = new String(requestBytes);

            // request line 앞에 오는 연속된 CRLF는 무시해야 함. (rfc9112#section-2.2)
            int requestLineStartIndex = 0;
            int headerEndIndex;
            Matcher emptyLineMatcher = EMPTY_LINE_PATTERN.matcher(requestString);
            Matcher headerEndMatcher = END_OF_HEADER_PATTERN.matcher(requestString);
            // request line 시작지점 찾기(연속된 CRLF 무시하기 위함)
            if (emptyLineMatcher.find()) {
                requestLineStartIndex = emptyLineMatcher.end();
            }
            // header 끝지점 찾기
            if (headerEndMatcher.find(requestLineStartIndex)) {
                headerEndIndex = headerEndMatcher.end();
            } else {
                throw new HttpException(HttpStatusCode.REQUEST_HEADER_FIELDS_TOO_LARGE, "Header Size가 너무 큽니다.");
            }

            // request-line + headers 문자열
            String headerSection = requestString.substring(requestLineStartIndex, headerEndIndex);

            // Request Line과 Header Lines 를 분리
            String[] tokens = headerSection.split(HTTP_LINE_SEPARATOR.value, 2);
            String requestLineString = tokens[0];
            String headerLines = tokens[1];

            if (!requestLineString.strip().equals(requestLineString))
                throw new BadRequest("Request Line 앞뒤로 공백이 있습니다.");

            // Request Line 문자열 파싱
            RequestLine requestLine = parseRequestLine(requestLineString);
            // Header Line 문자열 파싱
            HttpHeaders headers = parseHeaders(headerLines);


            String contentLengthString = headers.getHeader(CONTENT_LENGTH);
            byte[] body;
            // header를 읽을 때 미리 읽어들인 body의 길이
            int preReadBodyLength = requestBytes.length - headerEndIndex;
            if (contentLengthString != null) {
                try {
                    int contentLength = Integer.parseInt(contentLengthString);
                    body = new byte[contentLength];
                    // Content-Length 크기만큼 읽어들임
                    // 헤더 읽을 때 같이 읽어들인 부분은 복사
                    System.arraycopy(requestBytes, headerEndIndex, body, 0, preReadBodyLength);
                    in.readNBytes(body, preReadBodyLength, contentLength - preReadBodyLength);
                } catch (NumberFormatException e) {
                    throw new BadRequest(CONTENT_LENGTH.value + "값이 올바르지 않습니다.");
                } catch (IOException e) {
                    throw new InternalServerError("요청 Body를 읽는 중 오류가 발생했습니다.");
                }
            } else {
                byte[] buffer = in.readAllBytes();
                body = new byte[buffer.length + preReadBodyLength];
                System.arraycopy(requestBytes, headerEndIndex, body, 0, preReadBodyLength);
                System.arraycopy(buffer, 0, body, preReadBodyLength, buffer.length);
            }
            return new HttpRequest(requestLine.method(), requestLine.requestTarget(), requestLine.version(), headers, body);
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequest("Invalid Request", e);
        }
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
    private HttpHeaders parseHeaders(String headerLines) {
        HttpHeaders headers = new HttpHeaders();
        for (String line : headerLines.split(HTTP_LINE_SEPARATOR.value)) {
            String[] tokens = line.split(HEADER_KEY_SEPARATOR.value, 2);
            // header name과 : 사이에 공백이 있으면 400 응답해야 함. (rfc9112#section-5.1)
            if (tokens[0].endsWith(SP.value))
                throw new BadRequest("Header name 뒤에 공백이 있습니다.");
            headers.setHeader(tokens[0], tokens[1].strip());
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

    private record RequestLine(HttpMethod method, RequestTarget requestTarget, HttpVersion version) {

    }
}
