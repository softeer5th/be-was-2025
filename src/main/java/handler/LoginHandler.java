package handler;

import domain.User;
import domain.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;
import webserver.view.ModelAndTemplate;

import java.util.Optional;

import static enums.PageMappingPath.INDEX;

/**
 * 로그인 요청을 처리하는 핸들러
 */
public class LoginHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/login/index.html";
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final UserDao userDao;

    /**
     * 생성자
     *
     * @param userDao 사용자 조회 시 사용하는 UserDao 객체
     */
    public LoginHandler(UserDao userDao) {
        this.userDao = userDao;
    }


    /**
     * 로그인 페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    /**
     * 로그인 요청을 처리한다
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        LoginRequest body = request.getBody(LoginRequest.class).orElseThrow(() -> new BadRequest("Invalid Request Body"));
        log.debug("login request: {}", body);
        Optional<User> user = userDao.findUserById(body.userId());
        if (user.filter(u ->
                u.isPasswordCorrect(body.password())).isEmpty()) {
            return renderPageWithError();
        }
        HttpSession session = request.getSession();
        session.set(HttpSession.USER_KEY, user.get());
        return HttpResponse.redirect(INDEX.path);
    }

    private HttpResponse renderPageWithError() {
        HttpResponse response = new HttpResponse(HttpStatusCode.UNAUTHORIZED);
        ModelAndTemplate modelAndTemplate = new ModelAndTemplate(TEMPLATE_NAME);
        modelAndTemplate.setError("아이디 또는 비밀번호가 틀립니다.");
        response.renderTemplate(modelAndTemplate);
        return response;
    }

    private record LoginRequest(String userId, String password) {
    }
}
