package handler;

import enums.FileContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import manager.UserManager;
import model.User;
import request.BoardRequest;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.HttpRequestParser;

import static enums.HttpMethod.validPostMethod;
import static exception.ErrorCode.INVALID_AUTHORITY;
import static exception.ErrorCode.NOT_ALLOWED_PATH;

/**
 * BoardHandler는 HTTP 요청을 처리하여 게시판 관련 작업을 수행하는 핸들러 클래스입니다.
 * 이 클래스는 게시판의 글 작성, 좋아요, 즐겨찾기 기능을 처리하며,
 * 각 작업에 대한 적절한 HTTP 응답을 반환합니다.
 */
public class BoardHandler implements Handler {

    /**
     * 경로를 구분하기 위한 상수 (슬래시)
     */
    private static final String PATH_SPLIT_DELIMITER = "/";

    /**
     * 홈 페이지 경로
     */
    private static final String HOME_PATH = "/index.html";

    /**
     * BoardManager 인스턴스
     */
    private final BoardManager boardManager;

    /**
     * UserManager 인스턴스
     */
    private final UserManager userManager;

    /**
     * BoardHandler 생성자.
     * BoardManager와 UserManager의 싱글톤 인스턴스를 초기화합니다.
     */
    public BoardHandler() {
        this.boardManager = BoardManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    /**
     * HTTP 요청을 처리하는 메서드.
     * 요청 경로에 따라 게시글 작성, 좋아요, 즐겨찾기 기능을 처리하고,
     * 적절한 HTTP 응답을 반환합니다.
     *
     * @param request HTTP 요청 정보 객체
     * @return 처리된 HTTP 응답
     * @throws ClientErrorException 요청 경로가 유효하지 않거나 권한이 없는 경우 발생
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        HttpResponse response = new HttpResponse();

        // 게시글 작성
        if (request.getPath().equals(PATH.CREATE.endPoint)) {
            validPostMethod(request.getMethod());
            final BoardRequest boardRequest = HttpRequestParser.parseMultipartFormText(request.getHeaderValue(HttpHeader.CONTENT_TYPE.getName()), (String) request.getBody());

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            final User author = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));
            boardManager.save(boardRequest, author.getName());
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), HOME_PATH);

            // 게시글 좋아요
        } else if (request.getPath().startsWith(PATH.LIKE.endPoint)) {
            validPostMethod(request.getMethod());
            final String[] split = request.getPath().split(PATH_SPLIT_DELIMITER);
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));

            boardManager.likePost(postId, user.getId());
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), HOME_PATH);

            // 게시글 즐겨찾기
        } else if (request.getPath().startsWith(PATH.BOOKMARK.endPoint)) {
            validPostMethod(request.getMethod());
            final String[] split = request.getPath().split(PATH_SPLIT_DELIMITER);
            if (split.length != 4)
                throw new ClientErrorException(NOT_ALLOWED_PATH);
            int postId = Integer.parseInt(split[3]);

            final String cookie = request.getHeaderValue(HttpHeader.COOKIE.getName());
            User user = userManager.getUserFromSession(CookieParser.parseCookie(cookie))
                    .orElseThrow(() -> new ClientErrorException(INVALID_AUTHORITY));

            boardManager.bookmarkPost(postId, user.getId());
            response.setResponse(HttpStatus.FOUND, FileContentType.HTML_UTF_8);
            response.setHeader(HttpHeader.LOCATION.getName(), HOME_PATH);

            // 유효하지 않은 경로
        } else {
            throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
        }
        return response;
    }

    /**
     * 게시판 관련 경로들을 정의하는 열거형.
     * 게시글 작성, 좋아요, 즐겨찾기 경로를 나타냅니다.
     */
    private enum PATH {
        /**
         * 게시글 작성 경로
         */
        CREATE("/board"),

        /**
         * 게시글 좋아요 경로
         */
        LIKE("/board/like"),

        /**
         * 게시글 즐겨찾기 경로
         */
        BOOKMARK("/board/mark");

        /**
         * 경로 문자열
         */
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
