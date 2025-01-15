package webserver.response;

import util.enums.HttpStatusCode;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class ResponseWriter {
    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String CRLF = "\r\n";
    private static final String COLON = ": ";

    public static void write(DataOutputStream out, Response response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        try {
            out.writeBytes(HTTP_VERSION + statusCode.getCode() + " " + statusCode.getDescription() + CRLF);
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                out.writeBytes(header.getKey() + COLON + header.getValue() + CRLF);
            }
            if(response.hasBody()){
                writeBody(out, response.getBody());
            }
        } catch (IOException e) {
            throw e;
        } finally {
            out.flush();
        }
    }

    private static void writeBody(DataOutputStream out, byte[] body) throws IOException {
        out.writeBytes(CRLF);
        out.write(body, 0, body.length);
    }
}
