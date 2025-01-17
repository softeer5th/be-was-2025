package handler.dynamic_handler;

import db.Database;
import http.cookie.Cookie;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.session.SessionManager;
import model.User;

public class HomeDynamicHtmlHandler implements DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
    private static final String DYNAMIC_CONTENT = "<!-- DYNAMIC_CONTENT -->";

    @Override
    public HttpResponse handle(byte[] fileData, String extension, HttpRequest httpRequest) {
        String htmlContent = new String(fileData);

        Cookie cookie = httpRequest.getCookie("sessionId");

        String dynamicHtmlContent = null;

        if(cookie == null){
             dynamicHtmlContent = htmlContent.replace(DYNAMIC_CONTENT, createMenuItemContentNotLogin());
        }else{
            String userName = retrieveUserNameBySessionId(cookie.getValue());
            dynamicHtmlContent = htmlContent.replace(DYNAMIC_CONTENT, String.format(createMenuItemContentLogin(), userName));
        }

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

    private String createMenuItemContentNotLogin(){
        StringBuilder sb = new StringBuilder();
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n");
        sb.append("</li>\n");
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>\n");
        sb.append("</li>\n");
        return sb.toString();
    }

    private String createMenuItemContentLogin(){
        StringBuilder sb = new StringBuilder();
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">%s</a>\n");
        sb.append("</li>\n");
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_red btn_size_s\" href=\"/logout\">로그아웃</a>");
        sb.append("</li>\n");
        return sb.toString();
    }
}
