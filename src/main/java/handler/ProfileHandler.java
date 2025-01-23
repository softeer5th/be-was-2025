package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.UserManager;
import model.User;
import request.HttpRequestInfo;
import request.ImageRequest;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import static exception.ErrorCode.INVALID_AUTHORITY;

public class ProfileHandler implements Handler {
    private final UserManager userManager;


    public ProfileHandler() {

        this.userManager = UserManager.getInstance();
    }

    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();

        // 이미지 수정
        if (request.getPath().equals(PATH.UPDATE.endPoint)) {
            HttpMethod.validPostMethod(request.getMethod());
            final ImageRequest imageRequest = HttpRequestParser.parseMultipartFormImage(request.getHeaderValue(HttpHeader.CONTENT_TYPE.getName()), (String) request.getBody());

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));


            userManager.updateProfileImage(user, imageRequest);
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/mypage");
        } else if (request.getPath().equals(PATH.DELETE.endPoint)) {
            HttpMethod.validPostMethod(request.getMethod());

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));

            userManager.deleteProfileImage(user);

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), "/mypage");

        } else {
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }


    private enum PATH {
        UPDATE("/profile/update"),
        DELETE("/profile/delete"),
        BOOKMARK("/board/mark");

        private final String endPoint;

        PATH(String endPoint) {
            this.endPoint = endPoint;
        }
    }
}
