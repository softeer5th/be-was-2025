package handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.User;
import user.UserDao;
import webserver.enums.HttpStatusCode;
import webserver.enums.PageMappingPath;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.view.ModelAndTemplate;

// 회원가입 요청 처리
public class RegistrationHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/registration/index.html";
    private static final Logger log = LoggerFactory.getLogger(RegistrationHandler.class);
    private final UserDao userDao;

    public RegistrationHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        RegistrationRequest body = request.getBody(RegistrationRequest.class).orElseThrow(() -> new BadRequest("Invalid Request Body"));
        User user = body.toUser();
        log.debug("registration request: {}", user);
        // 중복 사용자 검사
        if (userDao.findUserById(user.getUserId()).isPresent()) {
            return renderRegistrationPageWithErrorMessage();
        }
        // 데이터베이스에 사용자 추가
        userDao.saveUser(user);
        return HttpResponse.redirect(PageMappingPath.INDEX.path);
    }

    private HttpResponse renderRegistrationPageWithErrorMessage() {
        HttpResponse response = new HttpResponse(HttpStatusCode.CONFLICT);
        ModelAndTemplate modelAndTemplate = new ModelAndTemplate(TEMPLATE_NAME);
        modelAndTemplate.setError("중복돤 아이디입니다.");
        response.renderTemplate(modelAndTemplate);
        return response;
    }

    record RegistrationRequest(String userId, String password, String name, String email) {
        public User toUser() {
            return User.create(userId, password, name, email);
        }
    }
}
