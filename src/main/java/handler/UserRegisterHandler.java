package handler;

import static http.HttpMethod.POST;

import exception.BaseException;
import exception.HttpErrorCode;
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
        User user = new User(userId, nickname, password, email);
        user.registerUser();
        logger.debug("Registered userId : {}, nickname : {}, email : {}", user.getUserId(), user.getNickname(), user.getEmail());

        response.setStatus(HttpStatus.FOUND);
        response.setHeaders("Location", "/registration/success.html");

        return response;
    }

}