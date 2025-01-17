package http.response;

import http.enums.ContentType;
import http.enums.ErrorMessage;
import http.enums.HttpResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseTest {

    @Test
    @DisplayName("HTTP Status 설정 후 빌더로 HttpResponse 생성 및 전송 테스트")
    void testStatusAndSend() throws IOException {
        HttpResponse response = new HttpResponse.Builder()
                .status(HttpResponseStatus.OK)
                .contentType(ContentType.HTML.getMimeType())
                .body("<h1>Hello World</h1>")
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.send(out);

        String result = out.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("HTTP/1.1 200 OK"), "상태 코드가 200 OK로 포함되어야 함");
        assertTrue(result.contains("Content-Type: text/html; charset=utf-8"), "Content-Type 헤더가 포함되어야 함");
        assertTrue(result.contains("<h1>Hello World</h1>"), "HTML 바디가 포함되어야 함");
    }

    @Test
    @DisplayName("쿠키 설정(setCookie) 테스트")
    void testSetCookie() throws IOException {
        Map<String, String> valueParams = new HashMap<>();
        valueParams.put("sid", "abc123");

        Map<String, String> optionParams = new HashMap<>();
        optionParams.put("Path", "/");
        optionParams.put("Max-Age", "3600");

        HttpResponse response = new HttpResponse.Builder()
                .status(HttpResponseStatus.OK)
                .setCookie(valueParams, optionParams)
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.send(out);

        String result = out.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Set-Cookie: "));
        assertTrue(result.contains("sid=abc123"));
        assertTrue(result.contains("Max-Age=3600"));
        assertTrue(result.contains("Path=/"));
    }

    @Test
    @DisplayName("errorResponse 빌더 메서드 테스트")
    void testErrorResponse() throws IOException {
        HttpResponse response = null;
        try {
            response = new HttpResponse.Builder()
                    .errorResponse(HttpResponseStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_PATH_AND_FILE)
                    .build();
        } catch (UnsupportedEncodingException e) {
            fail("UnsupportedEncodingException 발생: " + e.getMessage());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.send(out);

        String result = out.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("HTTP/1.1 404 Not Found"),
                "errorResponse: 404 상태 코드 헤더가 포함되어야 함");
        assertTrue(result.contains("<h1>404 Not Found - 존재하지 않는 경로 및 파일입니다.</h1>"),
                "에러 메시지가 HTML 바디에 포함되어야 함");
    }

    @Test
    @DisplayName("successResponse 빌더 메서드 테스트")
    void testSuccessResponse() throws IOException {
        HttpResponse response = null;
        try {
            response = new HttpResponse.Builder()
                    .successResponse(HttpResponseStatus.OK, ContentType.HTML.getMimeType(), "<h1>Success Body</h1>")
                    .build();
        } catch (UnsupportedEncodingException e) {
            fail("UnsupportedEncodingException 발생: " + e.getMessage());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.send(out);

        String result = out.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("HTTP/1.1 200 OK"),
                "successResponse: 200 상태 코드 헤더가 포함되어야 함");
        assertTrue(result.contains("Content-Type: text/html; charset=utf-8"),
                "Content-Type 헤더가 포함되어야 함");
        assertTrue(result.contains("<h1>Success Body</h1>"),
                "성공 시 바디 텍스트가 포함되어야 함");
    }

    @Test
    @DisplayName("redirectResponse 빌더 메서드 테스트")
    void testRedirectResponse() throws IOException {
        HttpResponse response = new HttpResponse.Builder()
                .redirectResponse(HttpResponseStatus.FOUND, "/main")
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.send(out);

        String result = out.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("HTTP/1.1 302 Found"),
                "302 Redirect 상태 코드 헤더가 포함되어야 함");
        assertTrue(result.contains("Location: /main"),
                "Location 헤더가 포함되어야 함");
        assertTrue(result.contains("Content-Length: 0"),
                "리다이렉트 시 Content-Length가 0이어야 함");
    }

    @Test
    @DisplayName("빌더에서 상태(status)를 설정하지 않은 경우 예외 발생 테스트")
    void testNoStatusSet() {
        assertThrows(IllegalStateException.class, () -> {
            // 상태를 설정하지 않고 build 시도
            new HttpResponse.Builder().build();
        }, "빌더에 상태를 설정하지 않으면 IllegalStateException이 발생해야 함");
    }
}