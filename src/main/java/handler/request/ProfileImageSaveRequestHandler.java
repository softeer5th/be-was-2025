package handler.request;

import db.UserDao;
import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.request.HttpRequest;
import http.request.MultipartPart;
import http.response.HttpResponse;
import http.session.SessionManager;

public class ProfileImageSaveRequestHandler implements RequestHandler{
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        Cookie cookie = httpRequest.getCookie("sessionId");
        Long userId = (Long)sessionManager.getSessionAttribute(cookie.getValue(), "userId");

        MultipartPart multipartPart = httpRequest.getMultipartPart("profileImage");
        transactionTemplate.executeWithoutResult(this::updateProfileImage, userId, multipartPart.getBody());

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .build();
    }

    private void updateProfileImage(Transaction transaction, Object[] args){
        Long userId = (Long) args[0];
        byte[] imageDataBytes = (byte[]) args[1];
        userDao.updateProfileImage(transaction, userId, imageDataBytes);
    }
}
