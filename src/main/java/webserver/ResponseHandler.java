package webserver;

import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseHandler {
    public static void respond(DataOutputStream dos, HTTPResponseHeader header, HTTPResponseBody body) throws IOException {
        dos.writeBytes(header.getVersion());
        try {
            dos.writeBytes(" " + header.getStatusCode() + "\r\n");

            for (String key: header.headers.keySet()) {
                dos.writeBytes(key + ": " + header.headers.get(key) + "\r\n");
            }

            if (body != null) {
                dos.writeBytes("\r\n");
                dos.write(body.getBody());
            }
        } catch (HTTPExceptions.Error500 e) {
            dos.writeBytes(" " + e.getMessage() + "\r\n");
        } finally {
            dos.flush();
        }
    }

    public static void respond(DataOutputStream dos, HTTPResponse response) throws IOException {
        HTTPResponseHeader responseHeader = response.getResponseHeader();
        HTTPResponseBody responseBody = response.getResponseBody();

        dos.writeBytes(responseHeader.getVersion());
        try {
            dos.writeBytes(" " + responseHeader.getStatusCode() + "\r\n");

            for (String key: responseHeader.headers.keySet()) {
                dos.writeBytes(key + ": " + responseHeader.headers.get(key) + "\r\n");
            }

            if (responseBody != null) {
                dos.writeBytes("\r\n");
                dos.write(responseBody.getBody());
            }
        } catch (HTTPExceptions.Error500 e) {
            dos.writeBytes(" " + e.getMessage() + "\r\n");
        } finally {
            dos.flush();
        }
    }
}
