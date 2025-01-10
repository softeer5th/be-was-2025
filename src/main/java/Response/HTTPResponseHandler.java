package Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import constant.HTTPCode;


public class HTTPResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(HTTPResponseHandler.class);
    private static final String defaultCharset = "UTF-8";
    private static final String defaultContentType = "text/html;charset=" + defaultCharset;

    // 정적파일 리턴 메서드
    public void responseSuccessHandler(DataOutputStream dos, HTTPCode httpCode, String resourceName, byte[] body) {
        try {
            String contentType = getContentType(resourceName);
            StringBuilder responseHeader = buildSuccessResponse(body.length,contentType,httpCode);

            dos.writeBytes(responseHeader.toString());
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    //String 리턴 메서드 (추후 필요에 따라 json 리턴 메서드 등도 추가예정)
    public void responseSuccessHandler(DataOutputStream dos, HTTPCode httpCode, String body) {
        try {
            String contentType = defaultContentType;
            int lengthOfBodyContent = body.getBytes(defaultCharset).length;
            StringBuilder response = buildSuccessResponse(lengthOfBodyContent,contentType,httpCode);
            response.append(body);

            dos.writeBytes(response.toString());
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    public void responseRedirectHandler(DataOutputStream dos, HTTPCode httpCode, String location) {
        try {
            StringBuilder response = buildRedirectResponse(httpCode,location);

            dos.writeBytes(response.toString());
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void responseFailHandler(DataOutputStream dos, HTTPCode httpCode){
        try {
            StringBuilder response = buildFailResponse(httpCode);

            dos.writeBytes(response.toString());
            dos.flush();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private StringBuilder buildSuccessResponse(int lengthOfBodyContent, String contentType, HTTPCode httpCode) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(httpCode.getStatusCode()).append(" ").append(httpCode.getReasonPhrase()).append("\r\n");
        response.append("Content-Type: ").append(contentType).append("\r\n");
        response.append("Content-Length: ").append(lengthOfBodyContent).append("\r\n");
        response.append("\r\n");
        return response;
    }

    private StringBuilder buildRedirectResponse(HTTPCode httpCode, String location){
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(httpCode.getStatusCode()).append(" ").append(httpCode.getReasonPhrase()).append("\r\n");
        response.append("Location: ").append(location).append("\r\n");
        response.append("\r\n");
        return response;
    }

    private StringBuilder buildFailResponse(HTTPCode httpCode){
        StringBuilder response = new StringBuilder();
        try {
            StringBuilder responseBodyBuilder = new StringBuilder()
                    .append("<html><body><h1>")
                    .append(httpCode.getResponseBody())
                    .append("</h1></body></html>");

            int lengthOfBodyContent = responseBodyBuilder.toString().getBytes(defaultCharset).length;
            response.append("HTTP/1.1 ").append(httpCode.getStatusCode()).append(" ").append(httpCode.getReasonPhrase()).append("\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("Content-Length: ").append(lengthOfBodyContent).append("\r\n");
            response.append("\r\n");
            response.append(responseBodyBuilder);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return response;
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
