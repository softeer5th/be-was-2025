package handler.dynamic_handler;

import db.Database;
import http.cookie.Cookie;
import http.request.HttpRequest;
import http.session.SessionManager;
import model.User;

public class HomeDynamicHtmlHandler implements DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
    private static final String DYNAMIC_CONTENT = "<!-- DYNAMIC_CONTENT -->";

    @Override
    public byte[] handle(byte[] fileData, HttpRequest httpRequest) {
        String htmlContent = new String(fileData);

        Cookie cookie = httpRequest.getCookie("sessionId");
        
        if(cookie == null){
            String dynamicHtmlContent = htmlContent.replace(DYNAMIC_CONTENT, createMenuItemContentNotLogin());
            return dynamicHtmlContent.getBytes();
        }

        String userName = retrieveUserNameBySessionId(cookie.getValue());

        String dynamicHtmlContent = htmlContent.replace(DYNAMIC_CONTENT, String.format(createMenuItemContentLogin(), userName));

        return dynamicHtmlContent.getBytes();
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
