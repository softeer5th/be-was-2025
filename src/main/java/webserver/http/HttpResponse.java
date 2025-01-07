package webserver.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(File file, DataOutputStream dos) {
        this.dos = dos;
        try {
            readFile(file);
            setHeader("Content-Type", "text/html;charset=utf-8");
            setHeader("Content-Length", String.valueOf(file.length()));
        } catch (IOException e){
            e.printStackTrace();
        }
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

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    private void writeStatusLine() throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
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
