package webserver.request;

import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.exception.InternalServerError;
import webserver.header.RequestHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static webserver.enums.ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
import static webserver.enums.HttpHeader.CONTENT_LENGTH;
import static webserver.enums.HttpHeader.CONTENT_TYPE;
import static webserver.enums.ParsingConstant.*;

// Request Body InputStream 을 읽어들여 파싱하는 클래스
public class RequestBody {
    private final RequestHeader headers;
    private final InputStream body;

    private boolean isBodyAlreadyRead = false;
    private byte[] rawBodyCache;
    private Map<String, String> mapBodyCache;
    private String stringBodyCache;

    public RequestBody(InputStream body, RequestHeader headers) {
        this.headers = headers;
        this.body = body;
    }

    // body를 String으로 반환
    public Optional<String> getBodyAsString() {
        if (stringBodyCache != null)
            return Optional.of(stringBodyCache);
        readBodyAsRaw();
        // body가 없는 경우
        if (rawBodyCache == null || rawBodyCache.length == 0)
            return Optional.empty();
        stringBodyCache = new String(rawBodyCache);
        return Optional.of(stringBodyCache);
    }

    // body를 Map으로 반환
    public Optional<Map<String, String>> getBodyAsMap() {
        if (mapBodyCache != null)
            return Optional.of(mapBodyCache);
        readBodyAsRaw();
        String contentType = headers.getHeader(CONTENT_TYPE.value);
        if (APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)) {
            mapBodyCache = parseXWwwFormUrlEncodedBody(rawBodyCache);
        } else {
            mapBodyCache = null;
        }
        return Optional.ofNullable(mapBodyCache);
    }

    // application/x-www-form-urlencoded 형식의 body를 파싱하여 Map으로 반환
    private Map<String, String> parseXWwwFormUrlEncodedBody(byte[] body) {
        if (body == null || body.length == 0)
            return null;
        String bodyString;
        try {
            bodyString = URLDecoder.decode(new String(body), DEFAULT_CHARSET.value);
        } catch (UnsupportedEncodingException e) {
            throw new BadRequest("Invalid Body Encoding");
        }
        Map<String, String> bodyMap = new HashMap<>();

        String[] tokens = bodyString.split(FORM_URLENCODED_SEPARATOR.value);
        for (String token : tokens) {
            if (token.isBlank())
                continue;
            if (token.startsWith(FORM_URLENCODED_KEY_VALUE_SEPARATOR.value)) {
                bodyMap.put(FORM_URLENCODED_DEFAULT_KEY.value, token.substring(1).strip());
            } else if (token.endsWith(FORM_URLENCODED_KEY_VALUE_SEPARATOR.value)) {
                bodyMap.put(token.substring(0, token.length() - 1).strip(), FORM_URLENCODED_DEFAULT_VALUE.value);
            } else {
                String[] keyValue = token.split(FORM_URLENCODED_KEY_VALUE_SEPARATOR.value, 2);
                bodyMap.put(keyValue[0].strip(), keyValue[1].strip());
            }
        }
        return bodyMap;
    }


    // body InputStream 을 읽어서 rawBodyCache에 저장
    private void readBodyAsRaw() {
        if (isBodyAlreadyRead) {
            return;
        }
        isBodyAlreadyRead = true;
        String contentLengthString = headers.getHeader(CONTENT_LENGTH.value);
        byte[] buffer;
        if (contentLengthString != null) {
            try {
                int contentLength = Integer.parseInt(contentLengthString);
                if (contentLength == 0)
                    return;
                buffer = new byte[contentLength];
                int readByte = 0, totalReadBytes = 0;
                do {
                    readByte = body.read(buffer, totalReadBytes, contentLength - totalReadBytes);
                    if (readByte == -1) {
                        throw new BadRequest("요청 Body의 크기가 " + CONTENT_LENGTH.value + " 값보다 작습니다.");
                    }
                    totalReadBytes += readByte;
                } while (totalReadBytes != contentLength);
                rawBodyCache = buffer;
            } catch (NumberFormatException e) {
                throw new BadRequest(CONTENT_LENGTH.value + "값이 올바르지 않습니다.");
            } catch (IOException e) {
                throw new InternalServerError("요청 Body를 읽는 중 오류가 발생했습니다.");
            }
        } else {
            // 메시지 본문은 포함하지만 Content-Length는 포함하지 않는 요청은 411로 거부할 수 있습니다. (rfc9112#section-6.3)
            throw new HttpException(HttpStatusCode.LENGTH_REQUIRED, CONTENT_LENGTH.value + " 헤더가 필요합니다.");
        }


    }
}
