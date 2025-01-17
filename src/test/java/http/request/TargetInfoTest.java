package http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TargetInfoTest {

    @Test
    @DisplayName("쿼리 파라미터가 있는 URI 처리 테스트")
    public void testValidUriWithParams() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "/index.html?key1=value1&key2=value2";
        TargetInfo targetInfo = new TargetInfo(uri);

        assertEquals("/index.html", targetInfo.getPath());
        assertEquals(2, targetInfo.getParams().size());
        assertEquals("value1", targetInfo.getParams().get("key1"));
        assertEquals("value2", targetInfo.getParams().get("key2"));
    }

    @Test
    @DisplayName("쿼리 파라미터가 없는 URI 처리 테스트")
    public void testUriWithoutParams() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "/about.html";
        TargetInfo targetInfo = new TargetInfo(uri);

        assertEquals("/about.html", targetInfo.getPath());
        assertTrue(targetInfo.getParams().isEmpty());
    }

    @Test
    @DisplayName("빈 쿼리 파라미터가 있는 URI 처리 테스트")
    public void testUriWithEmptyQuery() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "/contact.html?";
        TargetInfo targetInfo = new TargetInfo(uri);

        assertEquals("/contact.html", targetInfo.getPath());
        assertTrue(targetInfo.getParams().isEmpty());
    }

    @Test
    @DisplayName("인코딩된 쿼리 파라미터 처리 테스트")
    public void testUriWithEncodedParams() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "/search?query=java%20test&lang=en";
        TargetInfo targetInfo = new TargetInfo(uri);

        assertEquals("/search", targetInfo.getPath());
        assertEquals(2, targetInfo.getParams().size());
        assertEquals("java test", targetInfo.getParams().get("query"));
        assertEquals("en", targetInfo.getParams().get("lang"));
    }

    @Test
    @DisplayName("잘못된 쿼리 파라미터 처리 테스트")
    public void testUriWithInvalidQuery() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "/test?keyOnly";
        TargetInfo targetInfo = new TargetInfo(uri);

        assertEquals("/test", targetInfo.getPath());
        assertTrue(targetInfo.getParams().isEmpty());
    }
}