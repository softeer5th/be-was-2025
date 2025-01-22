package webserver.response;

import webserver.enums.HttpVersion;
import webserver.header.ResponseHeader;
import webserver.header.SetCookie;

import java.io.IOException;
import java.io.OutputStream;

import static webserver.enums.HttpHeader.SET_COOKIE;
import static webserver.enums.ParsingConstant.CRLF;

/**
 * HttpResponse 객체를 이용해 클라이언트에게 실제로 응답을 전송
 */
public class HttpResponseWriter {

    /**
     * HttpResponse 객체를 이용해 클라이언트에게 응답을 전송
     *
     * @param version  응답의 HTTP 버전
     * @param response 응답에 사용할 HttpResponse 객체
     * @param out      응답을 전송할 OutputStream
     */
    public void writeResponse(HttpVersion version, HttpResponse response, OutputStream out) {
        try {
            byte[] newLine = CRLF.value.getBytes();

            // status line
            out.write("%s %d %s".formatted(version.version, response.getStatusCode().statusCode, response.getStatusCode().reasonPhrase).getBytes());
            out.write(newLine);
            // response headers
            ResponseHeader header = response.getHeaders();
            for (String headerName : header.getHeaderNames()) {
                out.write("%s: %s".formatted(formatHeaderName(headerName), header.getHeader(headerName)).getBytes());
                out.write(newLine);
            }
            // set-cookie headers
            for (SetCookie setCookie : header.getSetCookies()) {
                out.write("%s: %s".formatted(SET_COOKIE.value, setCookie.toString()).getBytes());
                out.write(newLine);
            }
            // blank line
            out.write(newLine);
            // body
            response.getBody().writeBody(out);
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
        }
    }

    // 헤더 이름을 일반적인 이름으로 변경. EX) content-type -> Content-Type
    private String formatHeaderName(String headerName) {
        StringBuilder formattedName = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : headerName.toCharArray()) {
            if (capitalizeNext) {
                formattedName.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                formattedName.append(c);
            }
            if (c == '-') {
                capitalizeNext = true;
            }
        }

        return formattedName.toString();
    }
}
