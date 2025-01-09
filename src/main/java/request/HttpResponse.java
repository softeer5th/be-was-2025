package request;


import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private final HttpStatus httpStatus;
    private final DataOutputStream dos;
    private final byte[] body;
    private final String contentType;

    public HttpResponse(HttpStatus httpStatus, DataOutputStream dos,
                        byte[] body, String contentType) {
        this.httpStatus = httpStatus;
        this.dos = dos;
        this.body = body;
        this.contentType = contentType;
    }

    public void respond() throws IOException{
        makeHeader();
        makeBody();
        send();
    }

    private void makeHeader() throws IOException {
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 ").append(httpStatus.code()).append(' ').append(httpStatus.name()).append(" \r\n");
        header.append("Content-Type: ").append(contentType).append(";charset=utf-8\r\n");
        header.append("Content-Length: ").append(body.length).append("\r\n\r\n");
        dos.writeBytes(header.toString());
    }

    private void makeBody() throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private void send() throws IOException{
        dos.flush();
    }



}
