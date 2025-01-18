package handler;

import db.Database;
import model.User;
import webserver.enums.HttpStatusCode;
import webserver.exception.BadRequest;
import webserver.handler.HttpHandler;
import webserver.request.HttpRequest;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;
import webserver.view.ModelAndTemplate;

import java.util.Optional;

import static webserver.enums.PageMappingPath.INDEX;

public class LoginHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/login/index.html";
    private final Database database;

    public LoginHandler(Database database) {
        this.database = database;
    }

    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    @Override
    public HttpResponse handlePost(HttpRequest request) {
        LoginRequest body = request.getBody(LoginRequest.class).orElseThrow(() -> new BadRequest("Invalid Request Body"));
        Optional<User> user = database.findUserById(body.userId());
        if (user.filter(u ->
                u.isPasswordCorrect(body.password())).isEmpty()) {
            return renderLoginPageWithErrorMessage();
        }
        HttpSession session = request.getSession();
        session.set(HttpSession.USER_KEY, user.get());
        return HttpResponse.redirect(INDEX.path);
    }

    private HttpResponse renderLoginPageWithErrorMessage() {
        HttpResponse response = new HttpResponse(HttpStatusCode.UNAUTHORIZED);
        ModelAndTemplate modelAndTemplate = new ModelAndTemplate(TEMPLATE_NAME);
        modelAndTemplate.setError("아이디 또는 비밀번호가 틀립니다.");
        response.renderTemplate(modelAndTemplate);
        return response;
    }

    private record LoginRequest(String userId, String password) {
    }
}
