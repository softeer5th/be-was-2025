package webserver.interceptor;


import webserver.exception.LoginRequired;
import webserver.request.HttpRequest;
import webserver.session.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static enums.PageMappingPath.LOGIN;

/**
 * 특정한 페이지로의 로그인 없는 접근을 막는 인터셉터
 */
public class LoginRequiredPathInterceptor implements HandlerInterceptor {
    private static final String REDIRECT_URL = LOGIN.path;
    private final List<Pattern> loginRequiredPaths;

    /**
     * 로그인이 필요한 페이지 경로를 받아서 인스턴스를 생성한다.
     *
     * @param paths 로그인이 필요한 페이지 경로. path variable 형식을 사용할 수 있다.
     *              예) "/users/{userId}"
     */
    public LoginRequiredPathInterceptor(String... paths) {
        loginRequiredPaths = new ArrayList<>();
        for (String path : paths) {
            loginRequiredPaths.add(Pattern.compile(path.replaceAll("\\{[^/]+}", "[^/]+")));
        }
    }

    /**
     * 로그인이 필요한 페이지에 로그인하지 않은 경우 요청을 막는다
     *
     * @param request HTTP 요청
     * @param context 미사용
     * @return 요청이 그대로 반환된다.
     * @throws LoginRequired 로그인이 필요한 페이지에 로그인하지 않은 경우. LoginRequiredExceptionFilter 에서 처리된다.
     */
    @Override
    public HttpRequest preHandle(HttpRequest request, Context context) {
        Object loginUser = request.getSession().get(HttpSession.USER_KEY);
        String requestPath = request.getRequestTarget().getPath();
        // 로그인이 필요한 페이지에 로그인하지 않은 경우
        if (loginUser == null && loginRequiredPaths.stream().anyMatch(pattern -> pattern.matcher(requestPath).matches())) {
            throw new LoginRequired(REDIRECT_URL);
        }
        return request;
    }
}
