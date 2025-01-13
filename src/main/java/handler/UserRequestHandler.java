package handler;

import db.Database;
import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import request.UserCreateRequest;
import response.HttpResponse;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.ALREAD_EXIST_USERID;

public class UserRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);
    private static final String USER_REQUEST_PREFIX = "/user/";
    private static final String REDIRECT_URL = "http://localhost:8080/index.html";

    @Override
    public HttpResponse handle(final HttpRequestInfo request) {
        logger.debug("request : {} ", request);


        String path = request.getPath().substring(USER_REQUEST_PREFIX.length());
        HttpResponse response = new HttpResponse();

        if (request.getMethod().equals(POST) && path.startsWith("create")) {
            UserCreateRequest userCreateRequest = UserCreateRequest.of((String) request.getBody());

            createUser(userCreateRequest);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8, "successssssss");
            response.setHeaders(HttpHeader.LOCATION.getName(), REDIRECT_URL);
        }

        return response;
    }

    private void createUser(final UserCreateRequest request) {
        if (Database.findUserById(request.userId()) != null)
            throw new ClientErrorException(ALREAD_EXIST_USERID);

        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        Database.addUser(user);
    }
}
