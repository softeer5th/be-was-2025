package handler;

import static org.junit.jupiter.api.Assertions.*;

import exception.BaseException;
import exception.HttpErrorCode;
import http.HttpMethod;
import http.HttpRequestInfo;
import http.HttpResponse;
import http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class UserRegisterHandlerTest {

    private final UserRegisterHandler userRegisterHandler = new UserRegisterHandler();

    private static final HttpMethod VALID_HTTP_METHOD = HttpMethod.POST;
    private static final HttpMethod INVALID_HTTP_METHOD = HttpMethod.GET;
    private static final String VALID_REQUEST_PATH = "/users/register";
    private static final String VALID_QUERY_PARAM = "userId=yulee&nickname=uri&password=qwer1234!&email=1234%40example.com";

    private HttpRequestInfo createHttpRequest(HttpMethod method, String path, String body) throws IOException {
        String rawRequest =
                method + " " + path + " HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "\r\n" +
                        body;

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestInfo(inputStream);
    }
    @Test
    @DisplayName("회원가입 성공")
    void testHandleWithValidUserData() throws IOException {
        HttpRequestInfo httpRequestInfo = createHttpRequest(VALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);
        HttpResponse response = userRegisterHandler.handle(httpRequestInfo);

        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    @DisplayName("잘못된 HTTP Method인 경우")
    void testHandleWithInvalidHttpMethod() throws IOException {
        HttpRequestInfo httpRequestInfo = createHttpRequest(INVALID_HTTP_METHOD, VALID_REQUEST_PATH, VALID_QUERY_PARAM);

        BaseException baseException = assertThrows(BaseException.class, () -> userRegisterHandler.handle(httpRequestInfo));
        assertEquals(baseException.getMessage(), HttpErrorCode.INVALID_HTTP_METHOD.getMessage());
    }
}
