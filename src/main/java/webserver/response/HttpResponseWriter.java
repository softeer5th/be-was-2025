package webserver.response;

import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;
import webserver.exception.HttpException;
import webserver.request.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// HttpResponse 객체를 이용해 클라이언트에게 실제로 응답을 전송
public class HttpResponseWriter {

    // 일반적인 응답을 전송
    public void writeResponse(HttpRequest request, HttpResponse response, OutputStream out) {
        write(request.getVersion(), response, out);
    }

    // HttpException이 발생했을 때의 응답을 전송
    public void writeError(HttpVersion version, HttpException e, OutputStream out) {
        HttpResponse errorResponse = new HttpResponse(HttpStatusCode.of(e.getStatusCode()));
        errorResponse.setBody(e.getMessage());
        write(version, errorResponse, out);
    }

    private void write(HttpVersion version, HttpResponse response, OutputStream out) {
        try {
            // status line
            out.write("%s %d %s\r\n".formatted(version.version, response.getStatusCode().statusCode, response.getStatusCode().message).getBytes());
            // response headers
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                out.write("%s: %s\r\n".formatted(header.getKey(), header.getValue()).getBytes());
            }
            // blank line
            out.write("\r\n".getBytes());
            // body
            response.getBody().writeBody(out);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
        }
    }
}
