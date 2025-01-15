package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.FileReader;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class DynamicHomeHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(DynamicHomeHandler.class);

    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    private static final String REPLACE_TARGET = "{{user_info}}";

    private final UserManager userManager;
    public DynamicHomeHandler() {
        this.userManager = UserManager.getInstance();
    }


    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        log.debug("request : {}", request);
        String path = request.getPath();

        FileContentType extension = FileContentType.getExtensionFromPath(path);

        HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.OK);
        response.setContentType(extension);

        StringBuilder html = FileReader.readFileAsStringBuilder(STATIC_FILE_PATH + path)
                .orElseThrow(() -> new ClientErrorException(ErrorCode.FILE_NOT_FOUND));

        final String sessionId = CookieParser.parseCookie(request.getHeaderValue(HttpHeader.COOKIE.getName()));

        String dynamicHtmlContent = setDynamicHtmlContentByUserName(userManager.getNameFromSession(sessionId));
        String body = html.toString().replace(REPLACE_TARGET, dynamicHtmlContent);

        response.setBody(body);
        return response;
    }

    private String setDynamicHtmlContentByUserName(Optional<String> username) {

        if (username.isEmpty()) {
            return """
                    <ul class="header__menu">
                    <li class="header__menu__item">
                    <a class="btn btn_contained btn_size_s" href="/login/index.html">로그인</a>
                    </li>
                    <li class="header__menu__item">
                    <a class="btn btn_ghost btn_size_s" href="/registration/index.html">
                    회원 가입
                    </a>
                    </li>
                    </ul>
                    """;
        }

        final String name;
        name = URLDecoder.decode(username.get(), StandardCharsets.UTF_8);

        return """
                        <a> 안녕하세요. 반갑습니다..   </a>
                        <ul class="header__menu">
                        <li class="header__menu__item">
                        <a class="btn btn_contained btn_size_s" href="/mypage/index.html">
                        """
                        +
                        name
                        +
                        """
                                님</a>
                                </li>
                                <li class="header__menu__item">
                                </li>
                                </ul>
                                """;

    }
}
