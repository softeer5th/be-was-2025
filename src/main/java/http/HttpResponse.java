package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);

    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void writeStatusLine(HttpStatus httpStatus) {
        try {
            dos.writeBytes(String.format("HTTP/1.1 %d %s \r\n", httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void writeHeader(String name, String value) {
        if (headers.get(name.toLowerCase()) != null) {
            headers.compute(name.toLowerCase(), (k, existed) -> existed + value);
            return;
        }
        headers.put(name.toLowerCase(), value);
    }

    public void writeHeader(String name, int value) {
        writeHeader(name, String.valueOf(value));
    }

    public void writeBody(byte[] body) {
        this.body = body;
    }

    public void send() {
        try {
            for (String key : headers.keySet()) {
                dos.writeBytes(String.format("%s: %s\r\n", key, headers.get(key)));
            }
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
