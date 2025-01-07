package webserver.response;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

// thread safe
// HttpResponse 객체를 이용해 클라이언트에게 실제로 응답을 전송
public class HttpResponseWriter {

    public void write(HttpResponse response, BufferedWriter out) {
        try {
            out.write("%s %d %s\r\n".formatted(response.getVersion(), response.getStatusCode().statusCode, response.getStatusCode().message));
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                out.write("%s: %s\r\n".formatted(header.getKey(), header.getValue()));
            }
            out.write("\r\n");
            response.getBody().writeBody(out);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
        }
    }
}
