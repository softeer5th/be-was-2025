package handler;

import db.Database;
import exception.ErrorCode;
import exception.LoginException;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpStatus;
import model.User;

import java.util.Map;

public class UserLoginRequestHandler implements RequestHandler{
    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> loginData = httpRequest.convertBodyToMap();

        try{
            login(loginData.get("userId"), loginData.get("password"));

            return null;
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
