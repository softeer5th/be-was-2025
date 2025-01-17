package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    private static final Map<Integer, String> STATUS_CODE_MAP = Map.of(
            200, "HTTP/1.1 200 OK",
            302, "HTTP/1.1 302 Found",
            400, "HTTP/1.1 400 Bad Request",
            404, "HTTP/1.1 404 Not Found",
            405, "HTTP/1.1 405 Method Not Allowed",
            409, "HTTP/1.1 409 Conflict",
            415, "HTTP/1.1 415 Unsupported Media Type"
    );

    // 일반적인 상태 코드에 대한 응답
    public static void respond(DataOutputStream dos, byte[] body, String path, int statusCode) throws IOException {
        try {
            // 구현되지 않거나 존재하지 않은 상태 코드일 경우
            if (!STATUS_CODE_MAP.containsKey(statusCode)) {
                throw new IllegalArgumentException("Invalid status code: " + statusCode);
            }

            int bodyLength;
            // body가 빈 응답일 경우에 대한 예외처리
            if (body != null) {
                bodyLength = body.length;
            } else {
                bodyLength = 0;
            }

            String contentType = ContentTypeMapper.getContentType(path);

            responseHeader(dos, statusCode, contentType, bodyLength);
            responseBody(dos, body);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    // 특정 상태 코드별 응답
    // 상태 코드: 302
    public static void respond302(DataOutputStream dos, String location) throws IOException {
        try {
            dos.writeBytes(STATUS_CODE_MAP.get(302) + "\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // 공통 header를 응답하는 메서드
    private static void responseHeader(DataOutputStream dos, int statusCode, String contentType, int bodyLength) {
        try {
            dos.writeBytes(STATUS_CODE_MAP.get(statusCode) + "\r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + bodyLength + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    // body를 응답하는 메서드
    private static void responseBody(DataOutputStream dos, byte[] body) throws IOException {
        dos.write(body);
        dos.flush();
    }
}
