package util;

import common.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public void responseHeader(HttpStatus httpStatus, DataOutputStream dos, int lengthOfBodyContent, String filepath) {
        try {
            dos.writeBytes("HTTP/1.1 " + httpStatus.getCode() + httpStatus.getMessage() + " \r\n");
            dos.writeBytes("Content-Type: " + FileUtil.getContentType(filepath) + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
