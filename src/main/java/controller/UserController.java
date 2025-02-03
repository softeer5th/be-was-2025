package controller;

import db.UserDao;
import exception.BadRequestException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.HttpSession;
import wasframework.Mapping;
import webserver.httpserver.*;
import webserver.httpserver.header.Cookie;
import webserver.httpserver.header.MimeType;
import webserver.httpserver.header.SetCookie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static tag.MyPageBody.renderMyPage;
import static utils.FileUtils.getFile;
import static utils.FileUtils.getFileAsString;
import static wasframework.HttpSession.SESSION_ID;
import static webserver.httpserver.ContentType.*;
import static webserver.httpserver.ContentType.guessContentType;

public class UserController {

    public static final String USERNAME = "username";
    public static final String IMAGE = "image";
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final String FILENAME = "filename";

    /**
     * 로그인 페이지를 제공하는 핸들러.
     *
     * @param response 정상적으로 생성된 response 객체만 들어옴.
     * @throws IOException
     */
    @Mapping(path = "/login", method = HttpMethod.GET)
    public void loginPage(HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/index.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    /**
     * 로그인 처리 핸들러.
     * 로그인 처리한 뒤, 홈으로 리다이렉션한다.
     * 로그인 처리에 실패하면, /user/login_failed 창으로 리다이렉션한다.
     *
     * @param request  정상적으로 파싱된 request 객체
     * @param response 정상적으로 생성된 response 객체
     */
    @Mapping(path = "/login", method = HttpMethod.POST)
    public void login(HttpRequest request, HttpResponse response) {
        String inputUserId = request.getParameter(User.USER_ID);
        String inputPassword = request.getParameter(User.PASSWORD);
        if (inputUserId == null || inputPassword == null) {
            response.setLocation("/user/login_failed");
            return;
        }
        UserDao database = UserDao.USERS;
        Optional<User> byId = database.findById(inputUserId);
        if (byId.isEmpty()) {
            response.setLocation("/user/login_failed");
            return;
        }
        User userById = byId.get();

        if (!inputPassword.equals(userById.getPassword())) {
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
     *
     * @param response 정상적으로 생성된 response 객체
     * @throws IOException
     */
    @Mapping(path = "/user/login_failed", method = HttpMethod.GET)
    public void loginFailed(HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        File file = new File("src/main/resources/static/login/login_failed.html");
        byte[] readFile = getFile(file);
        response.setBody(readFile);
    }

    /**
     * 로그아웃 요청 처리 핸들러.
     * 로그인 된 회원의 경우, 세션 저장소에서 해당 회원의 세션을 지우고, 해당 회원의 쿠키를 만료시킴.
     *
     * @param request  정상적으로 파싱된 request 객체
     * @param response 정상적으로 생성된 response 객체
     */
    @Mapping(path = "/logout", method = HttpMethod.POST)
    public void logout(HttpRequest request, HttpResponse response) {
        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);
        if (sessionId != null) {
            String userId = HttpSession.get(sessionId);
            if (userId != null) {
                HttpSession.remove(sessionId);
                SetCookie newCookie = new SetCookie();
                newCookie.setMaxAge(0);
                response.setCookie(newCookie);
            }
        }
        response.setLocation("/");
    }

    /**
     * 마이 페이지를 보여주는 핸들러
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Mapping(path = "/mypage", method = HttpMethod.GET)
    public void mypage(HttpRequest request, HttpResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        if (sessionId == null || userId == null) {
            response.setLocation("/login");
            return;
        }

        UserDao database = UserDao.USERS;
        User findUser = database.findById(userId).orElseThrow(() -> new BadRequestException("존재하지 않는 id"));
        File file = new File("src/main/resources/static/mypage/index.html");
        String readFile = getFileAsString(file);
        readFile = renderMyPage(readFile, findUser.getProfileImage());
        response.setBody(readFile.getBytes());
    }

    /**
     * 비밀번호를 변경하는 핸들러.
     * 현재 프로필 이미지 수정과 결합도가 높음.
     * 분해 예정 TODO
     *
     * @param request
     * @param response
     */
    @Mapping(path = "/user/edit", method = HttpMethod.POST)
    public void changeProfile(HttpRequest request, HttpResponse response) {
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        Optional<MimeType> mimeType = request.getMimeType();
        if (mimeType.isEmpty() || mimeType.get().getType() != ContentType.MULTIPART_FORM_DATA) {
            throw new BadRequestException("적절한 타입이 입력되지 않음");
        }


        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);

        List<MultipartData> multipartData = MultipartDataParser.parse(request);
        String inputUsername = "";
        Optional<MultipartData> inputUsernameData = multipartData.stream()
                .filter(m -> m.getName().equals(USERNAME))
                .findFirst();


        if((inputUsernameData.isPresent() && inputUsernameData.get().getBody().length != 0)){
            inputUsername = new String(inputUsernameData.get().getBody());
        }

        /// 수정 요망 - getParameter -> form name username


        UserDao database = UserDao.USERS;
        Optional<User> byId = database.findById(userId);
        if (byId.isEmpty()) {
            response.setLocation("/error/401.html");
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            return;
        }

        User userById = byId.get();


        String imagePath = null;
        Optional<MultipartData> image = multipartData.stream()
                .filter(m -> m.getName().equals(IMAGE))
                .findFirst();
        if((image.isPresent() && image.get().getBody().length != 0)){
            String filename = image.get()
                    .getContentDisposition()
                    .getAttributeVariable(FILENAME)
                    .orElseThrow(()->new BadRequestException("파일 이름이 전달되지 않음"));

            List<ContentType> contentTypes = List.of(JPG, GIF, ICO, PNG, SVG);
            if(!contentTypes.contains(guessContentType(filename))){
                throw new BadRequestException("첨부된 파일이 이미지가 아닙니다.");
            }

            String fileExtension = filename.substring(filename.lastIndexOf('.'));

            imagePath = "/userProfileImages/" + userById.getUserId() + fileExtension;
            File file = new File("src/main/resources/static" + imagePath);
            try(FileOutputStream fos = new FileOutputStream(file)){
                fos.write(image.get().getBody());
                userById.changeProfileImage(imagePath);
            } catch (IOException e) {
                log.error("파일 저장 실패: ", e);
            }
        }

        if (!validateLogin(response, sessionId, userById, inputUsername)) return;
        // parameter -> multipartData 로 바꿔야함
        if (validatePassword(request, response, userId)) {
            userById.changePassword(request.getParameter("password"));
        }
        database.update(userById);
        response.setLocation("/");
    }

    private static boolean validateLogin(HttpResponse response, String sessionId, User userById, String inputUsername) {
        if (sessionId == null || !userById.getName().equals(inputUsername)) {
            response.setLocation("/error/401.html");
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            return false;
        }
        return true;
    }

    private boolean validatePassword(HttpRequest request, HttpResponse response, String userId) {
        if ((userId == null) ^ (request.getParameter("password") == null)) {
            response.setLocation("/error/401.html");
            response.setStatusCode(StatusCode.UNAUTHORIZED);
            return false;
        }
        if (!request.getParameter("password").equals(request.getParameter("passwordRewrite"))) {
            response.setLocation("/mypage");
            return false;
        }
        return true;
    }
}
