package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.CookieFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static webserver.httpserver.ContentType.X_WWW_FORM_URLENCODED;

public class HttpRequestFactory {
    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    public static final String QUERYPARAMETER_DELIMITER = "&";
    public static final String QUERYPARAMETER_KEVALUE_DELIMITER = "=";
    public static final String URI_QUERYPARAM_DELIMITER = "\\?";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String CONTENT_TYPE = "content-type";
    public static final String COOKIE = "cookie";
    private final CookieFactory cookieFactory = new CookieFactory();

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestFactory.class);


    /**
     * HTTP 요청 메시지를 파싱하여 저장한다.
     *
     * @param bis BufferedInputStream
     * @throws IOException 요청이 도중에 끊겼을 경우
     * @throws IllegalArgumentException 세가지 경우 중 한 경우를 만족하면 발생
     *                     요청 라인이 비어있는 경우
     *                     Request Line 의 내부에 3개 이상의 공백이 있는 경우
     *                     쿼리 파라미터가 key-value 쌍이 아닌 3개 이상의 튜플 형태를 가질 경우
     */
    public HttpRequest getHttpRequest(BufferedInputStream bis) throws IOException {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        parseRequestLine(bis, builder);
        ContentInfo contentInfo = parseHeader(bis, builder);
        parseBody(bis, builder, contentInfo);
        return builder.build();
    }


    private void parseRequestLine(BufferedInputStream bis, HttpRequest.Builder builder) throws IOException {
        String requestLine = readLine(bis);
        if (requestLine.isEmpty()) {
            throw new IllegalArgumentException("Method not supported");
        }
        String[] requestLineParts = requestLine.trim().split(" ");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("Method not supported");
        }
        builder.method(HttpMethod.valueOf(requestLineParts[0]));
        String[] uriParts = requestLineParts[1].split(URI_QUERYPARAM_DELIMITER);
        builder.uri(uriParts[0].trim());
        if (uriParts.length > 1) {
            parseQueryParameter(uriParts[1].trim(), builder);
        }
        builder.protocol(requestLineParts[2]);
    }


    private void parseQueryParameter(String rawQueryParams, HttpRequest.Builder builder) {
        if (rawQueryParams.isEmpty()) {
            return;
        }
        String[] queryParams = rawQueryParams.split(QUERYPARAMETER_DELIMITER);
        for (String queryParam : queryParams) {
            String[] paramPair = queryParam.split(QUERYPARAMETER_KEVALUE_DELIMITER);
            if (paramPair.length > 2) {
                throw new IllegalArgumentException("Invalid query parameter: " + queryParam);
            }
            String[] keyValue = new String[]{"", ""};
            System.arraycopy(paramPair, 0, keyValue, 0, paramPair.length);
            String key = URLDecoder.decode(keyValue[0].trim(), UTF_8);
            String value = URLDecoder.decode(keyValue[1].trim(), UTF_8);
            builder.addParameter(key, value);
        }
    }

    private ContentInfo parseHeader(BufferedInputStream bis, HttpRequest.Builder builder) throws IOException {
        String line;
        String contentType = "";
        String contentLength = "0";
        while (!(line = readLine(bis)).isEmpty()) {
            String[] parts = line.split(HEADER_KEY_VALUE_DELIMITER, 2);
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            logger.debug("{}: {}", key, value);
            builder.addHeader(key, value);
            if (COOKIE.equals(key)){
                builder.cookie(cookieFactory.create(value));
            }
            if(CONTENT_TYPE.equals(key)){
                contentType = value;
            }
            if(CONTENT_LENGTH.equals(key)){
                contentLength = value;
            }
        }
        return new ContentInfo(contentType, Integer.parseInt(contentLength));
    }

    private void parseBody(BufferedInputStream bis, HttpRequest.Builder builder, ContentInfo contentInfo) throws IOException {
        if (X_WWW_FORM_URLENCODED.getMimeType().equals(contentInfo.contentType)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < contentInfo.contentLength; i++) {
                sb.append((char) bis.read());
            }
            parseQueryParameter(sb.toString(), builder);
        }
    }

    private static String readLine(BufferedInputStream bis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int inputData = 0;
        while ((inputData = bis.read()) != -1) {
            if (inputData == '\n') {
                break;
            }
            sb.append((char) inputData);
        }
        return sb.toString().trim();
    }

    private record ContentInfo(String contentType, Integer contentLength){}
}
