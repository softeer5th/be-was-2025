package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import exception.ClientErrorException;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import request.UserCreateRequest;
import response.HttpResponse;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.METHOD_NOT_ALLOWED;

public class UserRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final String USER_REQUEST_PREFIX = "/user/";
    private static final String REDIRECT_URL = "http://localhost:8080/index.html";

    private final UserManager userManager;

    public UserRequestHandler() {
        userManager = new UserManager();
    }

    @Override
    public HttpResponse handle(final HttpRequestInfo request) {
        logger.debug("request : {} ", request);


        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (path.startsWith("create")) {
            validHttpMethodForCreateUser(request.getMethod());

            UserCreateRequest userCreateRequest = UserCreateRequest.of((String) request.getBody());

            userManager.createUser(userCreateRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "successssssss");
            response.setHeaders(HttpHeader.LOCATION.getName(), REDIRECT_URL);
        }

        return response;
    }

    private void validHttpMethodForCreateUser(HttpMethod method) {
        if (method != POST)
            throw new ClientErrorException(METHOD_NOT_ALLOWED);
    }


}
