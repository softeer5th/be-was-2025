package controller;

import db.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.Mapping;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;

import java.io.File;
import java.io.IOException;

import static utils.FileUtils.getFile;

public class SignUpController {

    private static final Logger log = LoggerFactory.getLogger(SignUpController.class);

    /**
     * 회원가입 필수정보 입력 페이지
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     * @throws IOException 파일이 존재
     */
    @Mapping(path = "/registration", method = HttpMethod.GET)
    public void registerPage(HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/registration/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    /**
     * 회원가입 처리 페이지.
     * {@code User} 객체를 새로 생성하여 DB에 저장하고 home으로 리다이렉션함.
     * @param request 정상적으로 파싱된 request 객체
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     */
    @Mapping(path = "/user/create", method = HttpMethod.POST)
    public void createUser(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter(User.USER_ID);
        String password = request.getParameter(User.PASSWORD);
        String name = request.getParameter(User.USERNAME);
        String email = request.getParameter(User.EMAIL);
        if(userId == null || password == null || name == null || email == null){
            response.setLocation("/registration");
            return;
        }
        User user = new User(userId, password, name, email);
        UserDao userDao = UserDao.USERS;
        if(userDao.findById(userId).isPresent()){
            response.setLocation("/registration");
            return;
        }

        userDao.save(user);

        log.info("User created: " + user);
        response.setLocation("/success");
    }

    /**
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     * @throws IOException
     */
    @Mapping(path = "/success", method = HttpMethod.GET)
    public void signUpSuccess(HttpResponse response) throws IOException {
        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html;charset=utf-8");

        File file = new File("src/main/resources/static/registration/success.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }
}
