package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import request.HttpRequestInfo;
import response.HttpResponse;

/**
 * 클라이언트의 요청을 처리하여 해당 경로로 리다이렉트하는 핸들러입니다.
 * <p>
 * 이 핸들러는 요청된 경로에 "/index.html"을 덧붙여 리다이렉트 응답을 생성합니다.
 * </p>
 */
public class RedirectHandler implements Handler {

    /**
     * 클라이언트 요청을 처리하여 리다이렉트 응답을 생성합니다.
     * <p>
     * 요청된 경로에 "/index.html"을 덧붙여서 클라이언트를 리다이렉트합니다.
     * </p>
     *
     * @param request 클라이언트의 HTTP 요청 정보
     * @return 리다이렉트 응답을 포함하는 {@link HttpResponse}
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();
        // FOUND (302) 상태 코드로 리다이렉트를 설정하고 HTML 응답을 생성
        response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
        // Location 헤더에 리다이렉트할 경로를 설정
        response.setHeader(HttpHeader.LOCATION.getName(), request.getPath() + "/index.html");
        return response;
    }
}