package http;

import http.enums.HttpStatus;
import http.enums.MimeType;
import util.FileUtil;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class HttpResponseResolver {
    private static final HttpResponseResolver INSTANCE = new HttpResponseResolver();

    public static HttpResponseResolver getInstance(){
        return INSTANCE;
    }
    public HttpResponseResolver(){
    }

    public void send200Response(DataOutputStream dos, String path, byte[] data) throws IOException {
        String extension = FileUtil.extractFileExtension(path);
        String contentType = Mime.getMimeType(extension);

        writeHeader(dos, HttpStatus.OK, contentType, data.length);
        writeBody(dos, data);
    }

    public void send404Response(DataOutputStream dos) throws IOException{
        String responseData = "Request file Not Found";

        byte[] byteData = responseData.getBytes();

        writeHeader(dos, HttpStatus.NOT_FOUND, "text/plain", byteData.length);
        writeBody(dos, byteData);
    }

    private void writeHeader(DataOutputStream dos, HttpStatus httpStatus, String contentType, int contentLength) throws IOException {
        dos.writeBytes(String.format("HTTP/1.1 %d %s\r\n", httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
        dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", contentType));
        dos.writeBytes(String.format("Content-Length: %d\r\n", contentLength));
        dos.writeBytes("\r\n");
    }

    private void writeBody(DataOutputStream dos, byte[] data) throws IOException {
        dos.write(data, 0, data.length);
        dos.flush();
    }
}
