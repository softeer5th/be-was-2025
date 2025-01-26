package handler;

import static http.HttpMethod.POST;

import db.UserDataManager;
import exception.BaseException;
import exception.HttpErrorCode;
import exception.UserErrorCode;
import http.HttpRequestInfo;
import http.HttpStatus;

import java.util.Map;

import model.User;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.QueryUtil;
import util.Validator;

public class UserRegisterHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterHandler.class);
    private static final int QUERY_SIZE = 4;

    private final UserDataManager userDataManager;

    public UserRegisterHandler(UserDataManager userDataManager) {
        this.userDataManager = userDataManager;
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        logger.info("UserRegisterHandler");
        if (request.getMethod() != POST) {
            throw new BaseException(HttpErrorCode.INVALID_HTTP_METHOD);
        }
        HttpResponse response = new HttpResponse();
        Map<String, String> queryParams = QueryUtil.parseQueryParams(request.getBody(), QUERY_SIZE);

        String userId = queryParams.get("userId");
        String nickname = queryParams.get("nickname");
        String password = queryParams.get("password");
        String email = queryParams.get("email");

        Validator.validateUser(userId, nickname, password, email);
        checkDuplicateUserId(userId);
        User user = new User(userId, nickname, password, email);
        userDataManager.addUser(user);
        logger.debug("Registered userId : {}, nickname : {}, email : {}", user.getUserId(), user.getNickname(), user.getEmail());

        response.setStatus(HttpStatus.FOUND);
        response.setHeaders("Location", "/registration/success.html");

        return response;
    }

    private void checkDuplicateUserId(String userId) {
        logger.error("checkDuplicateUserId : {}", userId);
        logger.error("findUserById : {}", userDataManager.findUserById(userId));
        if(userDataManager.findUserById(userId) != null) {
            throw new BaseException(UserErrorCode.DUPLICATE_USER_ID);
        }
    }

}