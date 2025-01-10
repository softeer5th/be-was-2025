package http;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    @DisplayName("응답 보내기 성공")
    void testSend() {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.OK);
        response.setContentType("text/html");
        response.setBody("Hello, World!");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(outputStream);

        response.send(dos);

        String output = outputStream.toString();
        assertTrue(output.contains("HTTP/1.1 200 OK"));
        assertTrue(output.contains("Content-Type: text/html"));
        assertTrue(output.contains("Hello, World!"));
    }
}
