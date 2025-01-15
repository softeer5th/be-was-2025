package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.Cookie;

import java.io.*;
import java.io.BufferedInputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static webserver.httpserver.ContentType.X_WWW_FORM_URLENCODED;

public class HttpRequest {
    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    public static final String QUERYPARAMETER_DELIMITER = "&";
    public static final String QUERYPARAMETER_KEVALUE_DELIMITER = "=";
    public static final String URI_QUERYPARAM_DELIMITER = "\\?";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String CONTENT_TYPE = "content-type";
    public static final String COOKIE = "cookie";
    private HttpMethod method;
    private String uri;
    private final Map<String, String> parameters = new HashMap<>();
    private String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(BufferedInputStream bis) throws IOException {
        parseRequestLine(bis);
        parseHeader(bis);
        parseBody(bis);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public Cookie getCookie(){
        if(!headers.containsKey(COOKIE)){
            return Cookie.NULL_COOKIE;
        }
        return new Cookie(getHeader(COOKIE));
    }

    public String getUri() {
        return uri;
    }

    public byte[] getBody() {
        return body;
    }

    private void parseRequestLine(BufferedInputStream bis) throws IOException {
        String requestLine = readLine(bis);
        if (requestLine.isEmpty()) {
            throw new IOException("Method not supported");
        }
        String[] requestLineParts = requestLine.trim().split(" ");
        if (requestLineParts.length != 3) {
            throw new IOException("Method not supported");
        }
        this.method = HttpMethod.valueOf(requestLineParts[0]);
        String[] uriParts = requestLineParts[1].split(URI_QUERYPARAM_DELIMITER);
        this.uri = uriParts[0].trim();
        if (uriParts.length > 1) {
            parseQueryParameter(uriParts[1].trim());
        }
        this.protocol = requestLineParts[2];
    }



    private void parseQueryParameter(String rawQueryParams) throws IOException {
        String[] queryParams = rawQueryParams.split(QUERYPARAMETER_DELIMITER);
        for (String queryParam : queryParams) {
            String[] paramPair = queryParam.split(QUERYPARAMETER_KEVALUE_DELIMITER);
            if (paramPair.length != 2) {
                throw new IOException("Invalid query parameter: " + queryParam);
            }
            String key = URLDecoder.decode(paramPair[0].trim(), UTF_8);
            String value = URLDecoder.decode(paramPair[1].trim(), UTF_8);
            this.parameters.put(key, value);
        }
    }

    private void parseHeader(BufferedInputStream bis) throws IOException {
        String line;
        while (!(line = readLine(bis)).isEmpty()) {
            String[] parts = line.split(HEADER_KEY_VALUE_DELIMITER,2);
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();
            logger.debug("{}: {}", key, value);
            headers.put(key, value);
        }
    }
    
    private void parseBody(BufferedInputStream bis) throws IOException {
        if (X_WWW_FORM_URLENCODED.getMimeType().equals(headers.get(CONTENT_TYPE))) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < Integer.parseInt(headers.get(CONTENT_LENGTH)); i++) {
                builder.append((char) bis.read());
            }
            parseQueryParameter(builder.toString());
        }
    }

    private static String readLine(BufferedInputStream bis) throws IOException {
        StringBuilder sb = new StringBuilder();
        int inputData = 0;
        while((inputData = bis.read())!=-1){
            if(inputData=='\n'){
                break;
            }
            sb.append((char)inputData);
        }
        return sb.toString().trim();
    }
}

