package webserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.http.cookie.Cookie;

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

    public HttpResponse(HttpRequest request, DataOutputStream dos) {
        this.request = request;
        this.dos = dos;
    }

    public void send() {
        try {
            writeStatusLine();
            writeCookies();
            writeHeaders();
            writeBody();
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    public void sendRedirect(String location) {
        setHeader("Location", location);
        setStatus(HttpStatus.FOUND);
    }

    public void setBody(File file) {
        try {
            readFile(file);
        } catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }

    public void setBody(String body){
        this.body = body.getBytes();
    }

    public void setContentLength(long contentLength) {
        setHeader("Content-Length", String.valueOf(contentLength));
    }

    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie.toString());
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void setOutPutStream(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
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
        dos.flush();
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
}
