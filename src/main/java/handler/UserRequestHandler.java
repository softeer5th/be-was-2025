package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.LoginException;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import request.UserCreateRequest;
import request.UserLoginRequest;
import response.HttpResponse;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.REQUEST_NOT_ALLOWED;

/*
 * 사용자와 관련된 기능(회원가입, 로그인, 로그아웃)을 담당하는 핸들러
 */
public class UserRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final String USER_REQUEST_PREFIX = "/user/";

    private static final String REDIRECT_URL = "http://localhost:8080/index.html";
    private static final String LOGIN_FAIL_URL = "http://localhost:8080/login/fail.html";

    private static final String SID = "SID";
    private static final String EXPIRED_COOKIE_TIMESTAMP = "Expires=Thu, 01 Jan 1970 00:00:00 GMT";
    private static final String DEFAULT_COOKIE_PATH = "Path=/";

    private final UserManager userManager;

    public UserRequestHandler() {
        userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(final HttpRequestInfo request) {
        logger.debug("request : {} ", request);


        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (path.startsWith(PATH.CREATE.endPoint)) {
            checkPostMethod(request.getMethod());

            UserCreateRequest userCreateRequest = UserCreateRequest.of((String) request.getBody());

            userManager.createUser(userCreateRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeaders(HttpHeader.LOCATION.getName(), REDIRECT_URL);
        } else if (path.startsWith(PATH.LOGIN.endPoint)) {
            checkPostMethod(request.getMethod());
            UserLoginRequest userLoginRequest = UserLoginRequest.of((String) request.getBody());

            try {
                final String sessionId = userManager.loginUser(userLoginRequest);

                response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
                response.setHeaders(
                        HttpHeader.LOCATION.getName(), REDIRECT_URL,
                        HttpHeader.SET_COOKIE.getName(), String.format("%s=%s; %s", SID, sessionId, DEFAULT_COOKIE_PATH)
                );
            } catch (LoginException e) {
                response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, e.getMessage());
                response.setHeaders(HttpHeader.LOCATION.getName(), LOGIN_FAIL_URL);
            }
        } else if (path.startsWith(PATH.LOGOUT.endPoint)) {
            userManager.logoutUser(request.getHeaderValue(HttpHeader.SET_COOKIE.getName()));

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeaders(
                    HttpHeader.LOCATION.getName(), REDIRECT_URL,
                    HttpHeader.SET_COOKIE.getName(), String.format("%s=; %s; %s", SID, EXPIRED_COOKIE_TIMESTAMP, DEFAULT_COOKIE_PATH)
            );
        }

        return response;
    }

    private void checkPostMethod(final HttpMethod method) {
        if (method != POST)
            throw new ClientErrorException(REQUEST_NOT_ALLOWED);
    }

    private enum PATH {
        CREATE("create"),
        LOGIN("login"),
        LOGOUT("logout");

        PATH(String endPoint) {
            this.endPoint = endPoint;
        }

        private final String endPoint;
    }


}
