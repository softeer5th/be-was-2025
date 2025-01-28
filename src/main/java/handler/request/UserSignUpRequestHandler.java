package handler.request;

import db.UserDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import exception.*;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpStatus;
import http.enums.MimeType;
import model.User;

import java.util.Map;

public class UserSignUpRequestHandler implements RequestHandler{
    private final UserDao userDao = UserDao.getInstance();
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> bodyMap = httpRequest.convertBodyToMap();
        try {
            transactionTemplate.executeWithoutResult(this::signUp, bodyMap.get("loginId"), bodyMap.get("password"), bodyMap.get("name"), bodyMap.get("email"));

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
                .httpStatus(HttpStatus.SEE_OTHER)
                .location("http://localhost:8080/")
                .build();
    }

    public void signUp(Transaction transaction, Object[] params){
        String loginId = (String) params[0];
        String password = (String) params[1];
        String name = (String) params[2];
        String email = (String) params[3];

        userDao.findByLoginId(transaction, loginId)
                .ifPresent((user) -> {throw new SignUpException(ErrorCode.ALREADY_EXIST_LOGIN_ID);});

        userDao.findByName(transaction, name)
                .ifPresent((user) -> {throw new SignUpException(ErrorCode.ALREADY_EXIST_USER_NAME);});

        userDao.save(transaction, new User(null, loginId, password, name, email, null));
    }

}
