package handler;

import enums.FileContentType;
import enums.HttpHeader;
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
import java.util.Optional;

import static enums.HttpStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

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

        String html = FileReader.readFileAsString(STATIC_FILE_PATH + path)
                .orElseThrow(() -> new ClientErrorException(ErrorCode.FILE_NOT_FOUND));

        final String sessionId = CookieParser.parseCookie(request.getHeaderValue(HttpHeader.COOKIE.getName()));

        String dynamicHtmlContent = setDynamicHtmlContentByUserName(userManager.getNameFromSession(sessionId));
        String body = html.replace(REPLACE_TARGET, dynamicHtmlContent);

        response.setResponse(OK, extension, body);
        return response;
    }

    private String setDynamicHtmlContentByUserName(Optional<String> username) {
        StringBuilder dynamicHtmlContent = new StringBuilder()
                .append("<ul class=\"header__menu\">")
                .append("<li class=\"header__menu__item\">");

        if (username.isEmpty()) {
            dynamicHtmlContent
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/login/index.html\">로그인</a>")
                    .append("</li>")
                    .append("<li class=\"header__menu__item\">")
                    .append("<a class=\"btn btn_ghost btn_size_s\" href = \"/registration/index.html\" >")
                    .append("회원 가입")
                    .append("</a>");
        } else {
            final String name;
            name = URLDecoder.decode(username.get(), UTF_8);
            dynamicHtmlContent
                    .append("<a class=\"btn btn_contained btn_size_s\" href=\"/mypage/index.html\">")
                    .append(name)
                    .append("님</a>");
        }

        dynamicHtmlContent
                .append("</li>")
                .append("</ul>");
        return dynamicHtmlContent.toString();
    }
}
