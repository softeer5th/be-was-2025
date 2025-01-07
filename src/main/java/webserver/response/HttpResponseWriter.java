package webserver.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

// thread safe
// HttpResponse 객체를 이용해 클라이언트에게 실제로 응답을 전송
public class HttpResponseWriter {

    public void write(HttpResponse response, OutputStream out) {
        try {
            // status line
            out.write("%s %d %s\r\n".formatted(response.getVersion(), response.getStatusCode().statusCode, response.getStatusCode().message).getBytes());
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
