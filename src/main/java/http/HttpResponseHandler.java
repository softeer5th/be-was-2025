package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);

    public static void responseHeader(DataOutputStream dos, int lengthOfBodyContent, String mimetype, HttpStatus httpStatus) {
        try {
            dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", mimetype));
            dos.writeBytes(String.format("Content-Length: %d\r\n", lengthOfBodyContent));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void redirect(DataOutputStream dos) {
        try {
            dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", 303, "See Other"));
            dos.writeBytes(String.format("Location: %s", "http://localhost:8080/main"));
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
