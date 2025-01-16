package controller;

import db.Database;
import model.User;
import wasframework.HttpSession;
import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;
import webserver.httpserver.header.Cookie;
import webserver.httpserver.header.SetCookie;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static utils.FileUtils.getFile;
import static wasframework.HttpSession.SESSION_ID;

public class UserController {

    /**
     * 로그인 페이지를 제공하는 핸들러.
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     * @throws IOException
     */
    @Mapping(path = "/login", method = HttpMethod.GET)
    public void loginPage(HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    /**
     * 로그인 처리 핸들러.
     * 로그인 처리한 뒤, 홈으로 리다이렉션한다.
     * 로그인 처리에 실패하면, /user/login_failed 창으로 리다이렉션한다.
     * @param request 정상적으로 파싱된 request 객체
     * @param response 정상적으로 생성된 response 객체
     */
    @Mapping(path = "/login", method = HttpMethod.POST)
    public void login(HttpRequest request, HttpResponse response) {
        String inputUserId = request.getParameter(User.USER_ID);
        String inputPassword = request.getParameter(User.PASSWORD);
        if (inputUserId == null || inputPassword == null) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setLocation("/user/login_failed");
            return;
        }
        User userById = Database.findUserById(inputUserId);
        if (userById == null) {
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            response.setLocation("/user/login_failed");
            return;
        }

        SetCookie cookie = new SetCookie();
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        String uuid = UUID.randomUUID().toString();
        cookie.setValue(SESSION_ID, uuid);
        HttpSession.put(uuid, inputUserId);
        response.setCookie(cookie);
        response.setLocation("/");
    }

    /**
     * 로그인 실패 창 서빙 핸들러
     * @param response 정상적으로 생성된 response 객체
     * @throws IOException
     */
    @Mapping(path = "/user/login_failed", method = HttpMethod.GET)
    public void loginFailed(HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.UNAUTHORIZED);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/login_failed.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    /**
     * 로그아웃 요청 처리 핸들러.
     * 로그인 된 회원의 경우, 세션 저장소에서 해당 회원의 세션을 지우고, 해당 회원의 쿠키를 만료시킴.
     * @param request 정상적으로 파싱된 request 객체
     * @param response 정상적으로 생성된 response 객체
     */
    @Mapping(path = "/logout", method = HttpMethod.POST)
    public void logout(HttpRequest request, HttpResponse response) {
        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);
        if (sessionId != null){
            String userId = HttpSession.get(sessionId);
            if(userId != null){
                HttpSession.remove(sessionId);
                SetCookie newCookie = new SetCookie();
                newCookie.setMaxAge(0);
                response.setCookie(newCookie);
            }
        }
        response.setLocation("/");
    }
}
