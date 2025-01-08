package webserver.http;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private HttpRequest request;

    private DataOutputStream dos;

    private HttpStatus status = HttpStatus.OK;

    private final Map<String, String> headers = new HashMap<>();

    private final byte[] body = new byte[8192];

    public HttpResponse() {}

    public HttpResponse(HttpRequest request, DataOutputStream dos) {
        this.request = request;
        this.dos = dos;
    }

    public void send() {
        try {
            writeStatusLine();
            writeHeaders();
            writeBody();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setBody(File file) {
        try {
            readFile(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void setContentLength(long contentLength) {
        setHeader("Content-Length", String.valueOf(contentLength));
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
        String responseBuilder = request.getVersion() +
                " " +
                status.getCode() +
                " " +
                status.getMessage() +
                "\r\n";

        dos.writeBytes(responseBuilder);
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

        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(body);
            if (bytesRead != file.length()) {
                throw new IOException("Failed to read file.");
            }
        }
    }
}
