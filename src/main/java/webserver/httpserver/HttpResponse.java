package webserver.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.header.Cookie;
import webserver.httpserver.header.SetCookie;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    public static final String HEADER_DELIMITER = ": ";
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private String protocol;
    private StatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final List<SetCookie> cookies = new ArrayList<>();
    private byte[] body;

    public HttpResponse() {
        setStatusCode(StatusCode.OK);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setCookie(SetCookie cookie) {
        cookies.add(cookie);
    }

    public byte[] getBody() {
        return body;
    }

    /**
     * body를 지정하고, Content-Length 헤더도 함께 지정하는 메소드
     * @param body
     */
    public void setBody(byte[] body) {
        this.body = body;
        if (body != null) {
            headers.put("Content-Length", String.valueOf(body.length));
        }
    }

    public void setLocation(String location) {
        setStatusCode(StatusCode.SEE_OTHER);
        setHeader("Location", location);
    }

    /**
     * DataOutputStream 에 현재까지 작성한 HTTP 응답 메시지를 작성하는 메소드
     * 스트림 작성 중 연결이 해제됐을 경우, 로그를 작성하고 종료한다.
     * @param dos
     */
    public void send(DataOutputStream dos) {
        try{
            dos.writeBytes(protocol + " " + statusCode.code + " " + statusCode.message + "\n");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                dos.writeBytes(header.getKey() + HEADER_DELIMITER + header.getValue() + "\n");
            }
            for (SetCookie cookie : cookies) {
                dos.writeBytes("Set-Cookie" + HEADER_DELIMITER + cookie.toString() + "\n");
            }
            dos.writeBytes("\n");
            if (body != null && body.length > 0) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            logger.debug("user disconnected");
        }
    }
}
