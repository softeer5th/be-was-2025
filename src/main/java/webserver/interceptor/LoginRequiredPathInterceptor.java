package webserver.interceptor;


import webserver.exception.LoginRequired;
import webserver.request.HttpRequest;
import webserver.session.HttpSession;

import java.util.Set;

import static webserver.enums.PageMappingPath.LOGIN;

// 특정한 페이지로의 로그인 없는 접근을 막는 인터셉터
public class LoginRequiredPathInterceptor implements HandlerInterceptor {
    private static final String REDIRECT_URL = LOGIN.path;
    private final Set<String> loginRequiredPaths;

    public LoginRequiredPathInterceptor(String... paths) {
        this.loginRequiredPaths = Set.of(paths);
    }

    @Override
    public HttpRequest preHandle(HttpRequest request, Context context) {
        Object loginUser = request.getSession().get(HttpSession.USER_KEY);
        String requestPath = request.getRequestTarget().getPath();
        // 로그인이 필요한 페이지에 로그인하지 않은 경우
        if (loginUser == null && loginRequiredPaths.contains(requestPath)) {
            throw new LoginRequired(REDIRECT_URL);
        }
        return request;
    }
}
