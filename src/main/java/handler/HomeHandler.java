package handler;

import enums.HttpHeader;
import enums.HttpMethod;
import exception.ClientErrorException;
import request.HttpRequestInfo;
import response.HttpResponse;

import static enums.FileContentType.HTML_UTF_8;
import static enums.HttpStatus.SEE_OTHER;

/**
 * HTTP GET 요청을 처리하고 홈 화면으로 리다이렉트하는 핸들러입니다.
 * <p>
 * 이 핸들러는 클라이언트가 홈 화면으로 리다이렉트되도록 응답을 구성합니다.
 * </p>
 */
public class HomeHandler implements Handler {

    // 홈 화면 URL을 환경 변수에서 가져옴
    private static final String HOME_URL = System.getenv("HOME_URL");

    /**
     * 클라이언트 요청을 처리하여 홈 화면으로 리다이렉트합니다.
     * <p>
     * HTTP GET 요청만 허용되며, 다른 HTTP 메서드가 요청되면 405 오류가 발생합니다.
     * </p>
     *
     * @param request 클라이언트의 HTTP 요청 정보
     * @return 리다이렉트 응답을 포함하는 {@link HttpResponse}
     * @throws ClientErrorException 만약 HTTP 메서드가 GET이 아닌 경우 발생
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpMethod.validGetMethod(request.getMethod());
        HttpResponse response = new HttpResponse(SEE_OTHER, HTML_UTF_8, "");
        response.setHeader(HttpHeader.LOCATION.getName(), HOME_URL); // 리다이렉트할 URL 설정
        return response;
    }
}
