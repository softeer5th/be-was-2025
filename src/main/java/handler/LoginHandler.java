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
import static util.CommonUtil.hasLength;

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
     * <pre>
     * 로그인 요청을 처리한다
     * body 형식
     * String userId: 사용자 아이디
     * String password: 사용자 비밀번호
     * </pre>
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        LoginRequest body = request.getBody(LoginRequest.class).orElseThrow(() -> new BadRequest("Invalid Request Body"));
        log.debug("login request: {}", body);
        body.validate();
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

    record LoginRequest(String userId, String password) {
        void validate() {
            if (!hasLength(userId, 3, 20)) {
                throw new BadRequest("아이디는 3자 이상 20자 이하여야 합니다.");
            }
            if (!hasLength(password, 3, 20)) {
                throw new BadRequest("비밀번호는 3자 이상 20자 이하여야 합니다.");
            }
        }
    }
}
