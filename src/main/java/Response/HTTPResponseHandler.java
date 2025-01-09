package Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import constant.HTTPCode;


public class HTTPResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(HTTPResponseHandler.class);

    public void responseSuccessHandler(DataOutputStream dos, int lengthOfBodyContent, String resourceName, byte[] body) {
        try {
            String contentType = getContentType(resourceName);
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void responseSuccessHandler(DataOutputStream dos, int lengthOfBodyContent, byte[] body) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void responseFailHandler(DataOutputStream dos, HTTPCode httpCode){
        try {
            dos.writeBytes("HTTP/1.1 " + httpCode.getHTTPCode() + " " + httpCode.getMessage() + "\r\n");
            dos.writeBytes("Content-Type: text/html\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes("<html><body><h1>" + httpCode.getMessage() + "</h1></body></html>");
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getContentType(String resourceName) {
        String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);

        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "text/javascript";
            case "ico":
                return "image/x-icon";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpeg";
            case "svg":
                return "image/svg+xml";
            default:
                return "text/plain";
        }
    }
}
