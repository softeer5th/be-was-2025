package webserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.cookie.Cookie;
import webserver.http.error.ErrorPageInfo;
import webserver.http.error.ErrorPageMapper;

import java.io.*;
import java.util.*;

public class HttpResponse {
    private final Logger logger = LoggerFactory.getLogger(HttpResponse.class);
    private HttpRequest request;
    private DataOutputStream dos;
    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private byte[] body;
    private boolean committed = false;
    private boolean errored = false;
    private String errorMessage;

    public HttpResponse(HttpRequest request, DataOutputStream dos) {
        this.request = request;
        this.dos = dos;
    }

    public void send() {
        if (committed) return;

        try {
            if (errored) {
                handleErrorPage(errorMessage);
            } else {
                sendResponse();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void sendRedirect(String location) {
        setHeader("Location", location);
        setStatus(HttpStatus.FOUND);
    }

    public void sendError(HttpStatus status, String message) {
        errored = true;
        setStatus(status);
        setErrorMessage(message);
    }

    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public void setBody(File file) {
        try {
            readFile(file);
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    public void setBody(String body){ this.body = body.getBytes(); }

    public void setContentLength(long contentLength) { setHeader("Content-Length", String.valueOf(contentLength)); }

    public void addCookie(Cookie cookie) { cookies.put(cookie.getName(), cookie.toString()); }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setContentType(String mimeType) {
        headers.put("Content-Type", mimeType);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    private void commit() throws IOException {
        if (committed) return;

        dos.flush();
        committed = true;
    }

    private void writeStatusLine() throws IOException {
        String statusLine = request.getVersion() +
                " " +
                status.getCode() +
                " " +
                status.getMessage() +
                "\r\n";

        dos.writeBytes(statusLine);
    }

    private void writeCookies() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String cookie : cookies.values()) {
            stringBuilder.append("Set-Cookie: ").append(cookie).append("\r\n");
        }

        dos.writeBytes(stringBuilder.toString());
    }

    private void writeHeaders() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            stringBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        dos.writeBytes(stringBuilder.toString());
        dos.writeBytes("\r\n");
    }

    private void writeBody() throws IOException {
        dos.write(body, 0, body.length);
    }

    private void readFile(File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File not found or is not a valid file");
        }

        body = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(body);
            if (bytesRead != file.length()) {
                throw new IOException("Failed to read file.");
            }
        }
    }

    private void handleErrorPage(String message) throws IOException {
        ErrorPageMapper errorPageMapper = ErrorPageMapper.getInstance();
        ErrorPageInfo customErrorPage = errorPageMapper.getErrorPage(status.getCode());

        if(customErrorPage == null) {
            String errorMessage = message == null ? status.getMessage() : message;
            renderDefaultErrorPage(status.getCode(), errorMessage);
        } else {
           sendRedirect(customErrorPage.location());
           send();
        }
    }

    private void renderDefaultErrorPage(int code, String message) throws IOException {
        if(committed) return;

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>")
                .append("<html lang='ko'>")
                .append("<head>")
                .append("<meta charset='UTF-8'>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>HTTP 상태 ").append(code).append(" - ").append(message).append("</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; background: #f8f9fa; padding: 20px; }")
                .append(".container { max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0px 0px 10px rgba(0,0,0,0.1); }")
                .append("h1 { background: #2f3b54; color: white; padding: 10px; font-size: 20px; }")
                .append(".info { padding: 10px; border-bottom: 1px solid #ddd; }")
                .append(".footer { text-align: center; font-size: 12px; color: gray; margin-top: 20px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class='container'>")
                .append("<h1>HTTP 상태 ").append(code).append(" – ").append(message).append("</h1>")
                .append("</div>")
                .append("</body>")
                .append("</html>");

        setBody(htmlBuilder.toString());
        sendResponse();
    }

    private void sendResponse() throws IOException {
        writeStatusLine();
        writeCookies();
        writeHeaders();
        writeBody();
        commit();
    }
}
