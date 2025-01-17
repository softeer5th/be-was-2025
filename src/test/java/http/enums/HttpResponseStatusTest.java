package http.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpResponseStatusTest {

    @Test
    @DisplayName("HTTP 상태 코드와 메시지 검증 테스트")
    public void testStatusCodesAndMessages() {
        assertEquals(200, HttpResponseStatus.OK.getStatusCode());
        assertEquals("OK", HttpResponseStatus.OK.getStatusMessage());

        assertEquals(201, HttpResponseStatus.CREATED.getStatusCode());
        assertEquals("Created", HttpResponseStatus.CREATED.getStatusMessage());

        assertEquals(400, HttpResponseStatus.BAD_REQUEST.getStatusCode());
        assertEquals("Bad Request", HttpResponseStatus.BAD_REQUEST.getStatusMessage());

        assertEquals(401, HttpResponseStatus.UNAUTHORIZED.getStatusCode());
        assertEquals("Unauthorized", HttpResponseStatus.UNAUTHORIZED.getStatusMessage());

        assertEquals(403, HttpResponseStatus.FORBIDDEN.getStatusCode());
        assertEquals("Forbidden", HttpResponseStatus.FORBIDDEN.getStatusMessage());

        assertEquals(404, HttpResponseStatus.NOT_FOUND.getStatusCode());
        assertEquals("Not Found", HttpResponseStatus.NOT_FOUND.getStatusMessage());
    }

    @Test
    @DisplayName("toString 메서드 출력 검증 테스트")
    public void testToString() {
        assertEquals("200 OK", HttpResponseStatus.OK.toString());
        assertEquals("201 Created", HttpResponseStatus.CREATED.toString());
        assertEquals("400 Bad Request", HttpResponseStatus.BAD_REQUEST.toString());
        assertEquals("401 Unauthorized", HttpResponseStatus.UNAUTHORIZED.toString());
        assertEquals("403 Forbidden", HttpResponseStatus.FORBIDDEN.toString());
        assertEquals("404 Not Found", HttpResponseStatus.NOT_FOUND.toString());
    }

    @Test
    @DisplayName("HTTP 상태 코드 개수 검증 테스트")
    public void testNumberOfStatuses() {
        assertEquals(6, HttpResponseStatus.values().length);
    }
}