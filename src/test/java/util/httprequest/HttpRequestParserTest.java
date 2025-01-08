package util.httprequest;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestParserTest {

    @Test
    void testParseRequestWithValidRequest() throws IOException {
        String startLine = "GET / HTTP/1.1";
        BufferedReader br = new BufferedReader(new StringReader(startLine));
        String result = HttpRequestParser.parseRequest(br);
        assertEquals(result, startLine);
    }

    @Test
    void testParseRequestWithInvalidRequest() throws IOException {
        String startLine = "INVALID REQUEST";
        BufferedReader br = new BufferedReader(new StringReader(startLine));
        String result = HttpRequestParser.parseRequest(br);
        assertEquals(result, startLine);
    }
}
