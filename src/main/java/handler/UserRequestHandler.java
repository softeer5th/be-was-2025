package handler;

import db.Database;
import exception.*;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.enums.MimeType;
import model.User;

import java.util.Collection;

public class UserRequestHandler implements  RequestHandler{
    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }
    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        try {
            signUp(httpRequest.getQueryParam("userId"), httpRequest.getQueryParam("password"), httpRequest.getQueryParam("name"));
        }catch(SignUpException e){
            byte[] errorMessageData = e.getMessage().getBytes();

            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MimeType.TEXT_PLAIN.getMimeType())
                    .contentLength(errorMessageData.length)
                    .body(errorMessageData)
                    .build();
        }
        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private void signUp(String userId, String password, String name){
        User user = Database.findUserById(userId);

        if(user != null){
            throw new SignUpException(ErrorCode.ALREADY_EXIST_USER_ID);
        }
        Collection<User> users = Database.findAll();
        for(User u: users){
            if(u.getName().equals(name)){
                throw new SignUpException(ErrorCode.ALREADY_EXIST_USER_NAME);
            }
        }
        user = new User(userId, password, name, null);
        Database.addUser(user);
    }

}
