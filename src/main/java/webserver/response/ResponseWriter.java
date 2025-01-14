package webserver.response;

import util.enums.HttpStatusCode;
import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ResponseWriter {
    private final String CRLF = "\r\n";
    private final String COLON = ": ";
    private final DataOutputStream out;
    private final Request request;
    private final String HTTP_VERSION = "HTTP/1.1 ";

    public ResponseWriter(DataOutputStream dos, Request request) {
        this.out = dos;
        this.request = request;
    }

    public void write(HttpStatusCode statusCode) throws IOException {
        try {
            out.writeBytes(HTTP_VERSION + statusCode.getCode() + " " + statusCode.getDescription() + CRLF);
            List<String> headers = statusCode.getHeaders();
            if (!headers.isEmpty()) {
                Response response = new Response(request);
                response.makeBody();
                for (String header : headers) {
                    out.writeBytes(header + COLON + response.getHeader(header) + CRLF);
                }
                writeBody(response.getBody());
            }
        } catch (IOException e) {
            throw e;
        } finally {
            out.flush();
        }
    }

    public void writeBody(byte[] body) throws IOException {
        out.writeBytes(CRLF);
        out.write(body, 0, body.length);
    }

    public void redirect(String location) throws IOException {
        out.writeBytes(HTTP_VERSION + HttpStatusCode.SEE_OTHER.getCode() + " " + HttpStatusCode.SEE_OTHER.getDescription() + CRLF);
        out.writeBytes("Location: " + location + CRLF + CRLF);
        out.flush();
    }
}
