package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseHandler.class);

    public static void response200Header(DataOutputStream dos, int lengthOfBodyContent, String mimetype) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", mimetype));
            dos.writeBytes(String.format("Content-Length: %d\r\n", lengthOfBodyContent));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static void response400Header(DataOutputStream dos, int lengthOfBodyContent, String mimetype) {
        try {
            dos.writeBytes("HTTP/1.1 400 Bad Request \r\n");
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
}
