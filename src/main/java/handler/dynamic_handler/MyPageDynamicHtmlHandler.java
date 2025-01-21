package handler.dynamic_handler;

import db.Database;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.SessionManager;
import model.User;

public class MyPageDynamicHtmlHandler implements  DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
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

        String dynamicHtmlContent = htmlContent.replace(USERNAME, retrieveUserNameBySessionId(cookie.getValue()));

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .contentType(MimeType.getMimeType(extension))
                .body(dynamicHtmlContent.getBytes())
                .build();
    }

    private String retrieveUserNameBySessionId(String sessionId){
        String userId = (String)sessionManager.getSessionAttribute(sessionId, "userId");
        User user = Database.findUserById(userId);
        return user.getName();
    }


}
