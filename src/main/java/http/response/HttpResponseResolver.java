package http.response;

import http.enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class HttpResponseResolver {
    private static final HttpResponseResolver INSTANCE = new HttpResponseResolver();

    public static HttpResponseResolver getInstance(){
        return INSTANCE;
    }
    public HttpResponseResolver(){
    }

    public void sendResponse(DataOutputStream dos, HttpResponse httpResponse) throws IOException {
        writeStatusLine(dos, httpResponse.getHttpStatus());
        writeHeaders(dos, httpResponse.getHeaders());
        writeBody(dos, httpResponse.getBody());
    }

    private void writeStatusLine(DataOutputStream dos, HttpStatus httpStatus) throws IOException {
        dos.writeBytes(String.format("HTTP/1.1 %d %s\r\n", httpStatus.getStatusCode(), httpStatus.getReasonPhrase()));
    }

    private void writeHeaders(DataOutputStream dos, Map<String, String> headers) throws IOException {
        for(String headerKey: headers.keySet()){
            dos.writeBytes(String.format("%s: %s\r\n", headerKey, headers.get(headerKey)));
        }
        dos.writeBytes("\r\n");
    }

    private void writeBody(DataOutputStream dos, byte[] body) throws IOException {
        if(body != null){
            dos.write(body, 0, body.length);
        }
        dos.flush();
    }
}
