package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MimeType;

import java.io.*;
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
            dos.writeBytes(String.format("%s %d %s\r\n", HttpHeader.PROTOCOL.value() ,httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
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

    public void writeBody(byte[] body, String mimeType) {
        writeHeader(HttpHeader.CONTENT_TYPE.value(), mimeType);
        writeHeader(HttpHeader.CONTENT_LENGTH.value(), body.length);
        writeBody(body);
    }

    public void writeBody(File file) throws IOException{
        InputStream is = new FileInputStream(file);
        byte[] body = is.readAllBytes();
        is.close();

        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        String mimeType = MimeType.valueOf(extension.toUpperCase()).getMimeType();
        writeHeader(HttpHeader.CONTENT_TYPE.value(), mimeType);
        writeHeader(HttpHeader.CONTENT_LENGTH.value(), body.length);
        writeBody(body);
    }

    public void send() {
        try {
            for (String key : headers.keySet()) {
                dos.writeBytes(String.format("%s: %s\r\n", key, headers.get(key)));
            }
            dos.writeBytes("\r\n");
            if (body != null) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void redirect(String path) {
        writeStatusLine(HttpStatus.SEE_OTHER);
        writeHeader(HttpHeader.LOCATION.value(), path);
        send();
    }
}
