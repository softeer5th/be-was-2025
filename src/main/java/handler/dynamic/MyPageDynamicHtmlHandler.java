package handler.dynamic;

import db.UserDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.SessionManager;
import model.User;

public class MyPageDynamicHtmlHandler implements  DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final String USERNAME = "<!-- USERNAME -->";

    @Override
    public HttpResponse handle(byte[] fileData, String extension, HttpRequest httpRequest) {
        String htmlContent = new String(fileData);

        Cookie cookie = httpRequest.getCookie("sessionId");

        if(cookie == null){
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.TEMPORARY_REDIRECT)
                    .location("http://localhost:8080/")
                    .build();
        }

        String username = transactionTemplate.execute(this::retrieveUserNameBySessionId, cookie.getValue());

        String dynamicHtmlContent = htmlContent.replace(USERNAME, username);

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .contentType(MimeType.getMimeType(extension))
                .body(dynamicHtmlContent.getBytes())
                .build();
    }

    private String retrieveUserNameBySessionId(Transaction transaction, Object[] args){
        String sessionId = (String) args[0];
        Long userId = (Long) sessionManager.getSessionAttribute(sessionId, "userId");

        User user = userDao.findById(transaction, userId).get();
        return user.getName();
    }


}
