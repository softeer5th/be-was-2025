package handler.request;

import db.UserDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
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
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserDao userDao = UserDao.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Map<String, String> loginData = httpRequest.convertBodyToMap();

        try{
            Long userId = transactionTemplate.execute(this::login, loginData.get("loginId"), loginData.get("password"));

            Session session = sessionManager.createSession();
            session.saveAttribute("userId", userId);

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


    public Long login(Transaction transaction, Object[] args){
        String loginId = (String) args[0];
        String password = (String) args[1];

        User user = userDao.findByLoginId(transaction, loginId).orElseThrow(() -> new LoginException(ErrorCode.ALREADY_EXIST_USER_NAME));

        if(!user.getPassword().equals(password)){
            throw new LoginException(ErrorCode.MISMATCH_PASSWORD);
        }

        return user.getId();
    }
}
