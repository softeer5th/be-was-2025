package http;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private final HttpStatus httpStatus;
    private final DataOutputStream dos;
    private final byte[] body;
    private final String contentType;
    private final StringBuilder headerBuilder;
    private static final String NOT_FOUND_PAGE = "<html><body><h1 style=\"text-align: center\">404 Not Found</h1></body></html>";

    public HttpResponse(HttpStatus httpStatus, DataOutputStream dos,
                        byte[] body, String contentType) {
        this.httpStatus = httpStatus;
        this.dos = dos;
        this.body = body;
        this.contentType = contentType;
        this.headerBuilder = new StringBuilder();
        addRequestLine();
    }

    public void respond() throws IOException {
        headerBuilder.append("\r\n");
        dos.writeBytes(headerBuilder.toString());
        if (body != null) makeBody();
        send();
    }

    public static void respond404(DataOutputStream dos) throws IOException {
        byte[] body = NOT_FOUND_PAGE.getBytes();
        HttpResponse httpResponse = new HttpResponse(HttpStatus.NOT_FOUND, dos, body, "text/html");
        httpResponse.addHeader("Content-Type", httpResponse.contentType);
        httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.body.length));
        httpResponse.respond();
    }

    private void addRequestLine() {
        headerBuilder.append("HTTP/1.1 ").append(httpStatus.getCode()).append(' ').append(httpStatus.getStatus()).append(" \r\n");
    }

    public void addHeader(String key, String value) {
        headerBuilder.append(key).append(": ").append(value).append("\r\n");
    }

    private void makeBody() throws IOException {
        dos.write(body, 0, body.length);
    }

    private void send() throws IOException {
        dos.flush();
    }
}
