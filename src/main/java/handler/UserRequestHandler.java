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
import static exception.ErrorCode.METHOD_NOT_ALLOWED;

public class UserRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final String USER_REQUEST_PREFIX = "/user/";
    private static final String REDIRECT_URL = "http://localhost:8080/index.html";
    private static final String LOGIN_FAIL_URL = "http://localhost:8080/login/fail.html";
    private static final String SID = "SID";

    private final UserManager userManager;

    public UserRequestHandler() {
        userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(final HttpRequestInfo request) {
        logger.debug("request : {} ", request);


        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (path.startsWith("create")) {
            checkPostMethod(request.getMethod());

            UserCreateRequest userCreateRequest = UserCreateRequest.of((String) request.getBody());

            userManager.createUser(userCreateRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "successssssss");
            response.setHeaders(HttpHeader.LOCATION.getName(), REDIRECT_URL);
        } else if (path.startsWith("login")) {
            checkPostMethod(request.getMethod());
            UserLoginRequest userLoginRequest = UserLoginRequest.of((String) request.getBody());

            try {
                final String sessionId = userManager.loginUser(userLoginRequest);

                response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "login success");
                response.setHeaders(
                        HttpHeader.LOCATION.getName(), REDIRECT_URL,
                        HttpHeader.SET_COOKIE.getName(), String.format("%s=%s; Path=/", SID, sessionId)
                );
            } catch (LoginException e) {
                response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, e.getMessage());
                response.setHeaders(HttpHeader.LOCATION.getName(), LOGIN_FAIL_URL);
            }
        } else if (path.equals("logout")) {
            userManager.logoutUser(request.getHeaderValue(HttpHeader.SET_COOKIE.getName()));

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "logoutSuccess");
            response.setHeaders(
                    HttpHeader.LOCATION.getName(), REDIRECT_URL,
                    HttpHeader.SET_COOKIE.getName(), String.format("%s=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/", SID)
            );
        }

        return response;
    }

    private void checkPostMethod(final HttpMethod method) {
        if (method != POST)
            throw new ClientErrorException(METHOD_NOT_ALLOWED);
    }


}
