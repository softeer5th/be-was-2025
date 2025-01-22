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

public class HomeDynamicHtmlHandler implements DynamicHtmlHandler{
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final String MENU = "<!-- MENU -->";
    private static final String WRITE_BUTTON = "<!-- WRITE_BUTTON -->";

    @Override
    public HttpResponse handle(byte[] fileData, String extension, HttpRequest httpRequest) {
        String htmlContent = new String(fileData);

        Cookie cookie = httpRequest.getCookie("sessionId");

        String dynamicHtmlContent = null;

        if(cookie == null){
             dynamicHtmlContent = htmlContent.replace(MENU, createMenuNotLogin());
        }else{

            String userName = transactionTemplate.execute(this::retrieveUserNameBySessionId, cookie.getValue());
            dynamicHtmlContent = htmlContent.replace(MENU, String.format(createMenuAfterLogin(), userName));
        }

        return new HttpResponse.Builder()
                .httpStatus(HttpStatus.OK)
                .contentType(MimeType.getMimeType(extension))
                .body(dynamicHtmlContent.getBytes())
                .build();
    }

    private String retrieveUserNameBySessionId(Transaction transaction, Object[] args){
        String sessionId = (String) args[0];
        Long userId = (Long)sessionManager.getSessionAttribute(sessionId, "userId");
        User user = userDao.findById(transaction, userId).get();

        return user.getName();
    }

    private String createMenuNotLogin(){
        StringBuilder sb = new StringBuilder();
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_contained btn_size_s\" href=\"/login\">로그인</a>\n");
        sb.append("</li>\n");
        sb.append("<li class=\"header__menu__item\">\n");
        sb.append("\t<a class=\"btn btn_ghost btn_size_s\" href=\"/registration\">회원 가입</a>\n");
        sb.append("</li>\n");
        return sb.toString();
    }

    private String createMenuAfterLogin(){
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
