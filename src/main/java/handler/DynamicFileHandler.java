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
/*
 * 로그인 여부에 따라 마이페이지 서빙을 관리하는 핸들러
 */
public class DynamicFileHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(DynamicFileHandler.class);

    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    private static final String REDIRECT_PATH = "/login/index.html";

    private final UserManager userManager;

    public DynamicFileHandler() {
        this.userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        log.debug("request : {}", request);
        HttpResponse response = new HttpResponse();

        final String sessionId = CookieParser.parseCookie(request.getHeaderValue(HttpHeader.COOKIE.getName()));
        final Optional<User> user = userManager.getUserFromSession(sessionId);

        user.ifPresentOrElse(
                // 값이 있을 경우 마이페이지 서빙
                (loginUser) -> serveRequestedFile(request, response),
                // 값이 없을 경우 홈 페이지로 리다이렉트
                () -> redirectToHomePage(response)
        );

        return response;
    }

    private void redirectToHomePage(HttpResponse response) {
        response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "");
        response.setHeader(HttpHeader.LOCATION.getName(), REDIRECT_PATH);
    }

    private void serveRequestedFile(HttpRequestInfo request, HttpResponse response) {
        byte[] body = FileReader.readFile(STATIC_FILE_PATH + request.getPath())
                .orElseThrow(() -> new ClientErrorException(FILE_NOT_FOUND));
        response.setResponse(HttpStatus.OK, FileContentType.HTML_UTF_8, body);
    }

}
