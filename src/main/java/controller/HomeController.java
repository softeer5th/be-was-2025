package controller;

import tag.HeaderMenu;
import wasframework.HttpSession;
import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;
import static utils.FileUtils.getFileAsString;
import static wasframework.HttpSession.SESSION_ID;

public class HomeController {

    /**
     * 홈 페이지 핸들러.
     * 사용자가 로그인 상태일 경우, 사용자에게 글쓰기와 로그아웃 버튼이 존재하는 페이지 반환
     * 사용자가 비로그인 상태일 경우, 사용자에게 회원가입과 로그인 버튼이 존재하는 페이지 반환
     * 현재 controller 패턴에 의한 요청의 handle + 뷰 구성 = 2가지 책임을 지고 있음
     *      뷰를 구성하는 View Resolver 구성 -> 스프링
     * @param request 정상적으로 파싱된 요청만 들어옴
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     * @throws IOException
     */
    @Mapping(path = "/", method = HttpMethod.GET)
    public void home(HttpRequest request, HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        String usernameTag = (sessionId != null && userId != null) ?
                getWelcomeTag(userId) : "";
        String loginWriteTag = (sessionId != null && userId != null) ? HeaderMenu.WRITE.getTag() : HeaderMenu.LOGIN.getTag();
        String signupLogoutTag = (sessionId != null && userId != null) ? HeaderMenu.LOGOUT.getTag() : HeaderMenu.SIGNUP.getTag();


        File file = new File("src/main/resources/static/index.html");
        String readFile = getFileAsString(file);
        String rendered = readFile.replace("${username}", usernameTag)
                .replace("${loginWrite}", loginWriteTag)
                .replace("${signupLogout}", signupLogoutTag);
        response.setBody(rendered.getBytes());
    }

    private static String getWelcomeTag(String userId) {
        return "<li class=\"header__menu__item\">\n" +
                "<a class=\"btn btn_ghost btn_size_s\" href=\"/mypage\">" +
                userId + "님, 환영합니다!</a> </li>";
    }
}
