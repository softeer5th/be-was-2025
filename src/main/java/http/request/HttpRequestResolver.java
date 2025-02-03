package http.request;

import http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ProtocolException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static http.request.HttpHeader.*;

public class HttpRequestResolver {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestResolver.class);
    private static final HttpRequestResolver INSTANCE = new HttpRequestResolver();
    private static final int MAX_BUFFER_SIZE = 8192 * 16; //8KB

    public static HttpRequestResolver getInstance(){
        return INSTANCE;
    }

    public HttpRequest parseHttpRequest(InputStream is) throws IOException {
        HttpRequest httpRequest = new HttpRequest();

        parseHttpRequestLine(is, httpRequest);
        parseHttpRequestHeader(is, httpRequest);
        parseHttpRequestBody(is, httpRequest);

        return httpRequest;
    }


    private void parseHttpRequestLine(InputStream is, HttpRequest httpRequest) throws IOException {
        String requestLine = readLine(is);

        if(requestLine.isEmpty())
            throw new ProtocolException("request line is empty");

        logger.debug("request line: {}", requestLine);

        String[] requestLineParts = requestLine.split("\\s+");
        String[] urlParts = requestLineParts[1].split("\\?");

        httpRequest.setMethod(requestLineParts[0]);
        httpRequest.setPath(urlParts[0]);
        httpRequest.setProtocol(requestLineParts[2]);

        if(urlParts.length == 2){
            parseQueryParameter(urlParts[1], httpRequest);
        }
    }

    private void parseQueryParameter(String queryString, HttpRequest httpRequest){
        String[] queryParams = queryString.split("&");

        for (String queryParam : queryParams){
            String[] queryParamPart = queryParam.split("=");
            // 한글이 전달되는 경우 url 인코딩 값이 넘어온다. 디코딩을 해서 저장한다.
            httpRequest.addQueryParam(queryParamPart[0], URLDecoder.decode(queryParamPart[1], StandardCharsets.UTF_8));
        }
    }

    private void parseHttpRequestHeader(InputStream is, HttpRequest httpRequest) throws IOException {
        StringBuilder sb = new StringBuilder();
        String headerLine;

        while(true){
            // 헤더 라인 읽기
            headerLine = readLine(is).trim();

            if(headerLine.isEmpty())
                break;

            sb.append(headerLine).append("\n");
            int colonIndex = headerLine.indexOf(':');

            if(colonIndex == -1){
                throw new ProtocolException("header line is missing a colon: " + headerLine);
            }

            String headerName = headerLine.substring(0, colonIndex).trim();
            String headerValue = headerLine.substring(colonIndex + 1).trim();

            if(headerName.equalsIgnoreCase(COOKIE.getName())){
                parseHttpRequestCookies(headerValue, httpRequest);
            }else{
                httpRequest.addHeader(headerName, headerValue);
            }

            if(headerName.equalsIgnoreCase(CONTENT_TYPE.getName())){
                String contentType = headerValue;
                if(contentType.startsWith("multipart/form-data")){
                    String boundary = extractBoundary(contentType);
                    httpRequest.setBoundary(boundary);
                }
            }
        }

        logger.debug("header: \n{}", sb.toString());
    }

    private void parseHttpRequestCookies(String cookieValueLine, HttpRequest httpRequest){
        String[] cookieValues = cookieValueLine.split(";");

        for(String cookieValue: cookieValues){
            Cookie cookie = Cookie.parseCookie(cookieValue);
            httpRequest.addCookie(cookie);
        }
    }

    private String extractBoundary(String contentType) throws IOException{
        String[] parts = contentType.split(";");

        if(parts[1].trim().startsWith("boundary=")){
            return parts[1].trim().substring(9);
        }

        throw new ProtocolException("Boundary not found in Content-Type");
    }
    private void parseHttpRequestBody(InputStream is, HttpRequest httpRequest) throws IOException {
        String contentType = httpRequest.getHeader(CONTENT_TYPE.getName());
        String contentLengthValue= httpRequest.getHeader(CONTENT_LENGTH.getName());

        // Content-Length 헤더가 존재하다면 바디를 읽는다.
        if (contentLengthValue != null) {
            int contentLength = Integer.parseInt(contentLengthValue);

            byte[] body = readBody(is, contentLength);

//            logger.debug("body: \n{}", URLDecoder.decode(new String(body, StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            httpRequest.setBody(body);
        }

        if(contentType != null && contentType.startsWith("multipart/form-data")){
            parseMultipartBody(httpRequest);
        }

    }

    private void parseMultipartBody(HttpRequest httpRequest) throws IOException {
        String boundary = httpRequest.getBoundary();
        byte[] body = httpRequest.getBody();
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);

        int boundaryIndex = 0;
        int nextBoundaryIndex;

        while(true){
             nextBoundaryIndex = findBoundaryIndex(body, boundaryBytes, boundaryIndex);

             if(nextBoundaryIndex == -1) break;

            byte[] partBytes = Arrays.copyOfRange(body, boundaryIndex, nextBoundaryIndex);

            parseMultipartPart(partBytes,  httpRequest);

            boundaryIndex = nextBoundaryIndex;
        }

    }

    private int findBoundaryIndex(byte[] body, byte[] boundaryBytes, int startIndex){
        for(int i = startIndex + 1; i < body.length; i++){
            boolean found = true;

            for (int j = 0; j < boundaryBytes.length; j++){
                if(body[i + j] != boundaryBytes[j]){
                    found = false;
                    break;
                }
            }
            if(found){
                return i;
            }
        }

        return -1;
    }

    private void parseMultipartPart(byte[] partBytes, HttpRequest httpRequest) throws IOException {
        StringBuilder sb = new StringBuilder();
        String headerLine, name;
        InputStream is = new ByteArrayInputStream(partBytes);

        // boundary 읽기
        String boundaryLine = readLine(is);

        MultipartPart multipartPart = new MultipartPart();

        while(true){
            headerLine = readLine(is).trim();

            if(headerLine.isEmpty()) break;

            sb.append(headerLine).append("\n");

            int colonIndex = headerLine.indexOf(':');

            if(colonIndex == -1){
                throw new ProtocolException("header line is missing a colon: " + headerLine);
            }

            String headerName = headerLine.substring(0, colonIndex).trim();
            String headerValue = headerLine.substring(colonIndex + 1).trim();
            multipartPart.addHeader(headerName, headerValue);

            if(headerName.equalsIgnoreCase("content-disposition")){
                String[] contentDispositionParts = headerValue.split(";");

                for(String part: contentDispositionParts){
                    part = part.trim();
                    if(part.startsWith("name=")){
                        name = part.substring(5).trim().replaceAll("\"", "");
                        httpRequest.addMultipartPart(name, multipartPart);
                        break;
                    }
                }
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[MAX_BUFFER_SIZE];
        int bytesRead;

        while((bytesRead = is.read(buffer)) != -1){
            baos.write(buffer, 0, bytesRead);
        }

        byte[] bodyBytes = baos.toByteArray();

        if (bodyBytes.length >= 2 && bodyBytes[bodyBytes.length - 2] == '\r' && bodyBytes[bodyBytes.length - 1] == '\n') {
            bodyBytes = Arrays.copyOf(bodyBytes, bodyBytes.length - 2);
        }

        multipartPart.setBody(bodyBytes);
    }

    private String readLine(InputStream is) throws IOException {
        return new String(readLineUntilCRLF(is), StandardCharsets.UTF_8);
    }

    private byte[] readLineUntilCRLF(InputStream is) throws IOException {
        int prevByte = -1, curByte;

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            while((curByte = is.read()) != -1){
                baos.write(curByte);
                if(prevByte == '\r' && curByte == '\n')
                    break;
                prevByte = curByte;
            }
            return baos.toByteArray();
        }
    }

    private byte[] readBody(InputStream is, int contentLength) throws IOException {
        int remainingLength = contentLength;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[MAX_BUFFER_SIZE];

        while(remainingLength > 0){
            int byteLength = is.read(buffer, 0, Math.min(buffer.length, remainingLength));

            baos.write(buffer, 0, byteLength);
            remainingLength -= byteLength;
        }

        return baos.toByteArray();
    }
}
