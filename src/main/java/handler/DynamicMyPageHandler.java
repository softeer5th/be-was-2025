package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import manager.UserManager;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.FileReader;

import java.util.Optional;

import static exception.ErrorCode.FILE_NOT_FOUND;

/**
 * 로그인 여부에 따라 마이페이지 서빙을 관리하는 핸들러.
 * 요청이 로그인한 사용자에게 마이페이지를 제공하거나, 로그인하지 않은 사용자에게는 로그인 페이지로 리다이렉트합니다.
 */
public class DynamicMyPageHandler implements Handler {

    /**
     * 로그 출력을 위한 Logger
     */
    private static final Logger log = LoggerFactory.getLogger(DynamicMyPageHandler.class);

    /**
     * 정적 파일 경로
     */
    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");

    /**
     * 로그인하지 않은 사용자를 위한 리다이렉트 경로
     */
    private static final String REDIRECT_PATH = "/login/index.html";

    /**
     * UserManager 인스턴스
     */
    private final UserManager userManager;

    /**
     * DynamicFileHandler 생성자.
     * UserManager의 싱글톤 인스턴스를 초기화합니다.
     */
    public DynamicMyPageHandler() {
        this.userManager = UserManager.getInstance();
    }

    /**
     * HTTP 요청을 처리하는 메서드.
     * 요청을 받은 후, 로그인 여부에 따라 마이페이지를 제공하거나 로그인 페이지로 리다이렉트합니다.
     *
     * @param request HTTP 요청 정보 객체
     * @return 처리된 HTTP 응답
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        log.debug("request : {}", request);
        HttpResponse response = new HttpResponse();

        // 쿠키에서 세션 ID를 파싱하여 사용자가 로그인했는지 확인
        final String sessionId = CookieParser.parseCookie(request.getHeaderValue(HttpHeader.COOKIE.getName()));
        final Optional<User> user = userManager.getUserFromSession(sessionId);

        // 사용자가 로그인했으면 마이페이지 서빙, 로그인하지 않았으면 홈 페이지로 리다이렉트
        user.ifPresentOrElse(
                // 값이 있을 경우 마이페이지 서빙
                (loginUser) -> serveRequestedFile(loginUser, request, response),
                // 값이 없을 경우 홈 페이지로 리다이렉트
                () -> redirectToHomePage(response)
        );

        return response;
    }

    /**
     * 사용자가 로그인하지 않은 경우 홈 페이지로 리다이렉트하는 메서드.
     *
     * @param response HTTP 응답 객체
     */
    private void redirectToHomePage(HttpResponse response) {
        response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "");
        response.setHeader(HttpHeader.LOCATION.getName(), REDIRECT_PATH);
    }

    /**
     * 로그인한 사용자가 요청한 파일을 서빙하는 메서드.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     */
    private void serveRequestedFile(User user, HttpRequestInfo request, HttpResponse response) {
        String body = FileReader.readFileAsString(STATIC_FILE_PATH + request.getPath())
                .orElseThrow(() -> new ClientErrorException(FILE_NOT_FOUND));
        String profile;
        if (user.getProfile() == null)
            profile = "<img class=\"profile\" alt=\"hmm\" src=\"../img/default.png\"/>";
        else
            profile = String.format(" <img class=\"profile\" alt=\"hmm\" src=\"../img/%s\"/>", user.getProfile());

        response.setResponse(HttpStatus.OK, FileContentType.HTML_UTF_8, body.replace("<!--image-->", profile));
    }
}