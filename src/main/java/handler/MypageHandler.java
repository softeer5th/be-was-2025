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

import static enums.PageMappingPath.INDEX;

/**
 * 마이페이지 요청을 처리하는 핸들러
 */
public class MypageHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/mypage/index.html";
    private static final Logger log = LoggerFactory.getLogger(MypageHandler.class);
    private final UserDao userDao;

    public MypageHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 마이페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    /**
     * 마이페이지 정보를 수정한다
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        UserUpdateRequest body = request.getBody(UserUpdateRequest.class).orElseThrow(() -> new BadRequest("잘못된 요청입니다."));
        HttpSession session = request.getSession();
        log.debug("user update request: {}, session: {}", body, session);

        User user = (User) session.get(HttpSession.USER_KEY);
        if (!body.isPasswordSame()) {
            return renderPageWithError();
        }
        user.update(body.name(), body.password());
        userDao.saveUser(user);

        return HttpResponse.redirect(INDEX.path);
    }

    private HttpResponse renderPageWithError() {
        HttpResponse response = new HttpResponse(HttpStatusCode.UNAUTHORIZED);
        ModelAndTemplate modelAndTemplate = new ModelAndTemplate(TEMPLATE_NAME);
        modelAndTemplate.setError("비밀번호 확인이 다릅니다.");
        response.renderTemplate(modelAndTemplate);
        return response;
    }

    private record UserUpdateRequest(String name, String password, String confirmPassword) {
        boolean isPasswordSame() {
            return password.equals(confirmPassword);
        }
    }
}
