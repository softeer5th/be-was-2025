package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Mapper;
import webserver.enums.ContentType;
import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.exception.HttpException;
import webserver.exception.InternalServerError;
import webserver.header.RequestHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import static webserver.enums.ContentType.APPLICATION_X_WWW_FORM_URLENCODED;
import static webserver.enums.HttpHeader.*;
import static webserver.enums.ParsingConstant.*;

/**
 * HTTP Request Body InputStream 을 읽어들여 파싱하는 클래스
 */
public class RequestBody {
    private static final Logger log = LoggerFactory.getLogger(RequestBody.class);
    private final RequestHeader headers;
    private final InputStream body;

    private boolean isBodyAlreadyRead = false;
    private byte[] rawBodyCache;
    private Map<String, String> mapBodyCache;
    private String stringBodyCache;

    /**
     * RequestBody 생성자
     *
     * @param body    Body 를 읽어들일 InputStream
     * @param headers Body 를 읽을 때 참고할 Request Header
     */
    public RequestBody(InputStream body, RequestHeader headers) {
        this.headers = headers;
        this.body = body;
    }

    /**
     * body를 파싱하여 객체로 반환
     *
     * @param clazz body를 파싱하여 반환할 객체의 클래스. String, Map, POJO(기본 생성자 필요) 가 가능하다.
     * @param <T>   body를 파싱하여 반환할 객체의 클래스
     * @return body 정보가 담긴 T 타입 객체. body가 없거나 지원하지 않는 타입인 경우 Optional.empty() 반환
     */
    public <T> Optional<T> getBody(Class<T> clazz) {
        if (clazz == String.class) {
            return (Optional<T>) getBodyAsString();
        }
        if (clazz == Map.class) {
            return (Optional<T>) getBodyAsMap();
        }
        String contentType = headers.getHeader(CONTENT_TYPE.value);
        if (APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)) {
            return getBodyAsMap().flatMap(map -> Mapper.mapToObject(map, clazz));
        }
        log.error("지원하지 않는 Content-Type 입니다. Content-Type: {}", contentType);
        return Optional.empty();
    }

    public Multipart getMultipart() {
        readBodyAsRaw();
        return parseMultipartFormDataBody(rawBodyCache);
    }

    private Map<String, String> getAttributeMap(String contentDisposition) {
        String[] contentDispositionTokens = contentDisposition.split(CONTENT_DISPOSITION_ATTRIBUTE_SEPARATOR.value);
        if (!"form-data".equals(contentDispositionTokens[0]))
            throw new IllegalArgumentException("Content-Disposition 헤더가 form-data가 아닙니다.");
        Map<String, String> attributes = new HashMap<>();
        for (int j = 1; j < contentDispositionTokens.length; j++) {
            String[] keyValue = contentDispositionTokens[j].split(CONTENT_DISPOSITION_ATTRIBUTE_KEY_VALUE_SEPARATOR.value);
            attributes.put(keyValue[0].strip(), keyValue[1].strip().replaceAll("\"", ""));
        }
        return attributes;
    }

    // body를 String으로 반환
    private Optional<String> getBodyAsString() {
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
    private Optional<Map<String, String>> getBodyAsMap() {
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

    private Multipart parseMultipartFormDataBody(byte[] body) {
        if (body == null || body.length == 0)
            return null;
        String contentType = headers.getHeader(CONTENT_TYPE.value);
        if (!contentType.startsWith(ContentType.MULTIPART_FORM_DATA.mimeType))
            throw new InternalServerError("Content-Type이 multipart/form-data가 아닙니다.");

        // boundary 찾기
        byte[] boundary = ("--" + contentType.split(MULTIPART_BOUNDARY.value)[1]).getBytes();
        byte[] crlf = "\r\n".getBytes();
        List<Multipart.FormData> formDataList = new ArrayList<>();

        // 현재 boundary의 시작 index
        int boundaryIndex = indexOf(body, boundary, 0);
        while (boundaryIndex < body.length) {
            // 다음 boundary의 시작 index
            int nextBoundaryIndex = indexOf(body, boundary, boundaryIndex + boundary.length);
            if (nextBoundaryIndex == -1) break;

            // 현재 boundary와 다음 boundary 사이의 데이터를 part로 저장
            byte[] part = Arrays.copyOfRange(body, boundaryIndex + boundary.length + crlf.length, nextBoundaryIndex);

            // part를 파싱하여 FormData로 변환
            Multipart.FormData formData = parseFormData(part);
            if (formData != null)
                formDataList.add(formData);
            boundaryIndex = nextBoundaryIndex;
        }
        return new Multipart(formDataList);

    }

    // array에서 target의 시작 index를 반환
    private int indexOf(byte[] array, byte[] target, int start) {
        outer:
        for (int i = start; i <= array.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j])
                    continue outer;
            }
            return i;
        }
        return -1;
    }

    private Multipart.FormData parseFormData(byte[] part) {

        // header의 끝 구분자인 CRLFCRLF의 index를 찾음
        int i = indexOf(part, CRLFCRLF.value.getBytes(), 0);

        // header를 분리
        String formDataHeader = new String(part, 0, i).strip();

        Map<String, String> headers = new HashMap<>();
        // header를 라인별로 분리하여 헤더 이름, 값을 Map에 저장
        String[] headerLines = formDataHeader.split(CRLF.value);
        for (String headerLine : headerLines) {
            String[] keyValue = headerLine.split(HEADER_KEY_SEPARATOR.value);
            headers.put(keyValue[0].strip(), keyValue[1].strip());
        }
        String contentDisposition = headers.get(CONTENT_DISPOSITION.value);
        // Content-Disposition 의 atturiubte를 파싱하여 Map으로 저장
        Map<String, String> attributes = getAttributeMap(contentDisposition);
        // body를 추출
        byte[] body = Arrays.copyOfRange(part, i + CRLFCRLF.value.getBytes().length, part.length - CRLF.value.getBytes().length);
        if (body.length == 0)
            return null;
        return new Multipart.FormData(headers, attributes, body);
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
