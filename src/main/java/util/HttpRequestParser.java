package util;

import config.ServerConfig;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpVersion;
import exception.ClientErrorException;
import exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);
    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final String PARAMETER_SEPARATOR = "=";
    private static final String HEADER_SEPARATOR = ":";
    private static final String QUERY_PARAMS = "&";
    private static final List<HttpVersion> supportedVersions = ServerConfig.getSupportedHttpVersions();


    public static HttpRequestInfo parse(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(inputStream);
        String requestLine = readLine(dis);

        // 1. 요청이 빈 줄인 경우, 이를 무시하고 다시 읽어들임
        while (requestLine.trim().isEmpty()) {
            requestLine = readLine(dis);
        }

        String[] requestInfo = requestLine.split(REQUEST_LINE_SEPARATOR);

        if (requestInfo.length != 3) {
            throw new ClientErrorException(ErrorCode.INVALID_HTTP_REQUEST);
        }

        HttpMethod method = HttpMethod.matchOrElseThrow(requestInfo[0]);
        String url = requestInfo[1];
        HttpVersion version = HttpVersion.matchOrElseThrow(requestInfo[2], supportedVersions);
        logger.debug("Request method = {}, url = {}, version = {}", method, url, version);

        // request의 내용을 로깅한다.
        // 4. 헤더 파싱
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = readLine(dis)).isBlank()) {
            String[] nameAndValue = headerLine.split(HEADER_SEPARATOR, 2);
            if (nameAndValue.length == 2) {
                headers.put(nameAndValue[0].toLowerCase(), nameAndValue[1].trim());
                logger.debug("{} = {}", nameAndValue[0], nameAndValue[1]);
            }
        }

        // 5. body parsing
        Object body;
        body = parseBody(dis, headers);

        if (body != null) {
            logger.debug("Request body = {}", body);
        }
        return new HttpRequestInfo(method, url, version, headers, body);
    }

    // DataInputStream을 사용하여 한 줄을 읽음
    private static String readLine(DataInputStream dis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        boolean hasCr = false; // \r이 발견되었는지 추적하는 변수

        while ((ch = dis.read()) != -1) {
            if (ch == '\r') { // \r이 발견되면
                hasCr = true; // \r을 발견
            } else if (ch == '\n' && hasCr) { // \n이 오고, 이전에 \r이 있었다면
                break; // \r\n을 찾았다면 줄 끝
            } else {
                // \r만 있는 경우는 그냥 \r을 추가하고, 그 외 문자는 그대로 추가
                sb.append((char) ch);
                hasCr = false; // \r이 없으면 초기화
            }
        }

        return !sb.isEmpty() ? sb.toString() : ""; // 줄의 내용이 있으면 반환
    }


    // 본문 파싱
    private static String parseBody(DataInputStream dis, Map<String, String> headers) throws IOException {
        String body = null;

        // 5.1 Content-Length가 있는 경우 본문을 해당 길이만큼 읽어온다
        if (headers.containsKey(HttpHeader.CONTENT_LENGTH.getName())) {
            int contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.getName()));
            byte[] bodyBytes = new byte[contentLength];
            dis.readFully(bodyBytes);
            body = new String(bodyBytes);
        }

        return body;
    }

    public static Map<String, String> parseParamString(String paramString) {
        String[] params = paramString.split(QUERY_PARAMS);
        Map<String, String> paramMap = new HashMap<>();

        for (String param : params) {
            String[] nameAnyKey = param.split(PARAMETER_SEPARATOR);
            if (nameAnyKey.length != 2) {
                throw new ClientErrorException(ErrorCode.MISSING_FIELD);
            }
            paramMap.put(nameAnyKey[0], nameAnyKey[1]);
        }
        return paramMap;
    }

    public static String parseMultipartFormText(String headerValue, String body) {
        final String boundary = getBoundaryFromContentType(headerValue);
        final String[] multipart = body.split("--" + boundary);
        final String[] fileData = multipart[1].split("\r\n\r\n", 2);
        return fileData[1].trim();
    }

    public static String getBoundaryFromContentType(String contentType) {
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            String[] parts = contentType.split(";");
            for (String part : parts) {
                if (part.trim().startsWith("boundary=")) {
                    return part.split("=")[1].trim();
                }
            }
        }
        return null;
    }
}

