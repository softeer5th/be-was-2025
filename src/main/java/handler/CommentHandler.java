package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.CommentManager;
import manager.UserManager;
import model.User;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import java.util.Map;

import static enums.HttpMethod.POST;
import static exception.ErrorCode.INVALID_AUTHORITY;
import static exception.ErrorCode.NOT_ALLOWED_PATH;

/**
 * CommentHandler는 HTTP 요청을 처리하여 댓글 작성 기능을 수행하는 핸들러 클래스입니다.
 * 요청에 포함된 경로와 메서드에 따라 댓글을 게시글에 추가합니다.
 */
public class CommentHandler implements Handler {

    /**
     * CommentManager 인스턴스
     */
    private final CommentManager commentManager;

    /**
     * UserManager 인스턴스
     */
    private final UserManager userManager;
    private final static String HOME_PATH = "/index.html";

    /**
     * CommentHandler 생성자.
     * CommentManager와 UserManager의 싱글톤 인스턴스를 초기화합니다.
     */
    public CommentHandler() {
        this.commentManager = CommentManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    /**
     * HTTP 요청을 처리하는 메서드.
     * 요청 경로가 댓글 작성 경로와 일치하며, HTTP 메서드가 POST일 경우 댓글을 작성합니다.
     *
     * @param request HTTP 요청 정보 객체
     * @return 처리된 HTTP 응답
     * @throws ClientErrorException 요청 경로가 유효하지 않거나 권한이 없는 경우 발생
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();

        // 댓글 작성
        if (request.getPath().startsWith(PATH.WRITE.endPoint) && request.getMethod() == POST) {
            final String[] split = request.getPath().split(PATH.PATH_SPLIT_DELIMITER.endPoint);
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);

            final Map<String, String> map = HttpRequestParser.parseParamString((String) request.getBody());
            final String comment = map.get("comment");

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User author = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));

            commentManager.save(postId, comment, author.getName());

            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), HOME_PATH);
        } else {
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }

    /**
     * 댓글 관련 경로들을 정의하는 열거형.
     * 댓글 작성 경로를 정의합니다.
     */
    private enum PATH {
        /**
         * 댓글 작성 경로
         */
        WRITE("/comment/write"),

        /**
         * 경로 구분자
         */
        PATH_SPLIT_DELIMITER("/");

        private final String endPoint;

        /**
         * PATH 열거형 생성자.
         *
         * @param endPoint 경로 문자열
         */
        PATH(String endPoint) {
            this.endPoint = endPoint;
        }
    }
}
