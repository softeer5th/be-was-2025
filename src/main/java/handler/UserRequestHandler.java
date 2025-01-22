package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import request.UserCreateRequest;
import request.UserLoginRequest;
import response.HttpResponse;

import static enums.HttpMethod.validPostMethod;

/**
 * 사용자 관련 요청(회원가입, 로그인, 로그아웃)을 처리하는 핸들러 클래스.
 * <p>
 * 이 클래스는 HTTP 요청의 경로에 따라 사용자의 회원가입, 로그인, 로그아웃 작업을 수행합니다.
 * 회원가입 시 새로운 사용자를 생성하고, 로그인 시 세션을 생성하여 쿠키를 설정하며,
 * 로그아웃 시 세션을 만료시켜 쿠키를 삭제합니다.
 * </p>
 */
public class UserRequestHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final String USER_REQUEST_PREFIX = "/user/";

    private static final String REDIRECT_URL = "http://localhost:8080/index.html";

    private static final String SID = "SID";
    private static final String EXPIRED_COOKIE_TIMESTAMP = "Expires=Thu, 01 Jan 1970 00:00:00 GMT";
    private static final String DEFAULT_COOKIE_PATH = "Path=/";

    private final UserManager userManager;

    /**
     * 생성자. `UserManager`의 인스턴스를 초기화합니다.
     */
    public UserRequestHandler() {
        userManager = UserManager.getInstance();
    }

    /**
     * 사용자 요청을 처리합니다.
     * <p>
     * 요청의 경로에 따라 회원가입, 로그인, 로그아웃 기능을 처리하며,
     * 각 작업 후 적절한 리다이렉트를 포함한 HTTP 응답을 생성합니다.
     * </p>
     *
     * @param request 클라이언트의 HTTP 요청 정보
     * @return HTTP 응답
     */
    @Override
    public HttpResponse handle(final HttpRequestInfo request) {
        logger.debug("request : {} ", request);

        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (path.startsWith(PATH.CREATE.endPoint)) {
            validPostMethod(request.getMethod());

            UserCreateRequest userCreateRequest = UserCreateRequest.of((String) request.getBody());
            userManager.createUser(userCreateRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), REDIRECT_URL);
        } else if (path.startsWith(PATH.LOGIN.endPoint)) {
            validPostMethod(request.getMethod());
            UserLoginRequest userLoginRequest = UserLoginRequest.of((String) request.getBody());
            final String sessionId = userManager.loginUser(userLoginRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(
                    HttpHeader.LOCATION.getName(), REDIRECT_URL,
                    HttpHeader.SET_COOKIE.getName(), String.format("%s=%s; %s", SID, sessionId, DEFAULT_COOKIE_PATH)
            );

        } else if (path.startsWith(PATH.LOGOUT.endPoint)) {
            userManager.logoutUser(request.getHeaderValue(HttpHeader.SET_COOKIE.getName()));

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(
                    HttpHeader.LOCATION.getName(), REDIRECT_URL,
                    HttpHeader.SET_COOKIE.getName(), String.format("%s=; %s; %s", SID, EXPIRED_COOKIE_TIMESTAMP, DEFAULT_COOKIE_PATH)
            );
        }

        return response;
    }

    /**
     * 사용자 요청에 대한 경로를 나타내는 enum.
     */
    private enum PATH {
        CREATE("create"),
        LOGIN("login"),
        LOGOUT("logout");

        private final String endPoint;

        PATH(String endPoint) {
            this.endPoint = endPoint;
        }
    }
}