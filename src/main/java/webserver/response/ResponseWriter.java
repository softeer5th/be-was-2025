package webserver.response;

import model.HttpStatusCode;
import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class ResponseWriter {
    private final String CRLF = "\r\n";
    private final String COLON = ": ";
    private final DataOutputStream out;
    private final RequestParser request;
    private final String HTTP_VERSION = "HTTP/1.1";

    public ResponseWriter(DataOutputStream dos, RequestParser request) {
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
}
