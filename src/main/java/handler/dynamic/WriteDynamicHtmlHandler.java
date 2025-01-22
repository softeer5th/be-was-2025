package handler.dynamic;

import db.Database;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.SessionManager;
import model.User;

import java.nio.charset.StandardCharsets;

public class WriteDynamicHtmlHandler implements DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
    private static final String USERNAME = "<!-- USERNAME -->";

    @Override
    public HttpResponse handle(byte[] fileData, String extension, HttpRequest httpRequest) {
        String htmlContent = new String(fileData, StandardCharsets.UTF_8);

        Cookie cookie = httpRequest.getCookie("sessionId");

        if(cookie == null){
            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.FOUND)
                    .location("http://localhost:8080/login")
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
