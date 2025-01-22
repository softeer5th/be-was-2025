package handler.request;

import db.Database;
import exception.ErrorCode;
import exception.LoginException;
import http.cookie.Cookie;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpStatus;
import http.session.Session;
import http.session.SessionManager;
import model.User;

import java.util.Map;

public class UserLoginRequestHandler implements RequestHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> loginData = httpRequest.convertBodyToMap();

        try{
            login(loginData.get("userId"), loginData.get("password"));

            Session session = sessionManager.createSession();
            session.saveAttribute("userId", loginData.get("userId"));

            Cookie cookie = new Cookie("sessionId", session.getSessionId());
            cookie.setPath("/");

            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.SEE_OTHER)
                    .location("http://localhost:8080/")
                    .setCookie(cookie)
                    .build();

        }catch(LoginException e){
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.SEE_OTHER)
                    .location("http://localhost:8080/login/login_failed.html")
                    .build();
        }
    }


    public void login(String userId, String password){
        User user = Database.findUserById(userId);

        if(user == null){
            throw new LoginException(ErrorCode.NOT_FOUND_USER_BY_USER_ID);
        }

        if(!user.getPassword().equals(password)){
            throw new LoginException(ErrorCode.MISMATCH_PASSWORD);
        }
    }
}
