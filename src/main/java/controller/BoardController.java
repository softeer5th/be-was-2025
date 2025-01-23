package controller;

import db.BoardDao;
import db.UserDao;
import exception.NotFoundException;
import model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.HttpSession;
import wasframework.Mapping;
import wasframework.PathVariable;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;
import webserver.httpserver.StatusCode;
import webserver.httpserver.header.Cookie;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import static tag.BoardBody.renderBoard;
import static tag.HeaderMenu.renderHeaderMenu;
import static utils.FileUtils.getFile;
import static utils.FileUtils.getFileAsString;
import static wasframework.HttpSession.SESSION_ID;

public class BoardController {

    public static final int START_NUMBER = 1;
    private static final Logger log = LoggerFactory.getLogger(BoardController.class);
    public static final String CONTENTS = "contents";

    /**
     * 게시글 검색을 위한 핸들러
     * @param id 게시글의 id
     *           존재하지 않는 id일 경우, 404 Not Found 페이지 던짐
     * @param request
     * @param response
     * @throws NotFoundException id 값이 잘못됐을 경우 던짐 (0 이하일 경우)
     */
    @Mapping(path = "/board/{id}", method = HttpMethod.GET)
    public void boardPage(@PathVariable("id") long id, HttpRequest request, HttpResponse response) {
        response.setHeader("Content-Type", "text/html; charset=utf-8");
        BoardDao boardDao = BoardDao.BOARDS;
        UserDao userDao = UserDao.USERS;
        if (boardDao.getBoardSize() < id) {
            File file = new File("src/main/resources/static/error/404.html");
            byte[] readFile = null;
            try {
                readFile = getFile(file);
            } catch (IOException e) {
                log.error("기본 페이지 탐색 예외 발생:", e);
            }
            response.setBody(readFile);
            return;
        }
        if (id < START_NUMBER){
            throw new NotFoundException("존재하지 않는 리소스");
        }

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        File file = new File("src/main/resources/static/board/index.html");
        String readFile = null;
        try {
            readFile = getFileAsString(file);
        } catch (IOException e) {
            log.error("기본 페이지 탐색 예외 발생:", e);
            return;
        }


        Board findBoard = boardDao.findById(id).orElseThrow(IllegalArgumentException::new);
        Long boardId = findBoard.getBoardId();
        String contents = findBoard.getContents();
        String writer = findBoard.getWriter();
        String writerProfileImagePath = "userProfileImages/" + writer;
        String imagePath = findBoard.getImagePath();


        String rendered = renderHeaderMenu(readFile, sessionId, userId);
        rendered = renderBoard(rendered, boardId, contents, writer, writerProfileImagePath, imagePath);
        response.setBody(rendered.getBytes());
    }

    /**
     * 게시글 작성 페이지
     * @param request
     * @param response
     * @throws IOException
     */
    @Mapping(path= "/article", method = HttpMethod.GET)
    public void postPage(HttpRequest request, HttpResponse response) throws IOException {

        response.setStatusCode(StatusCode.OK);
        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        File file = new File("src/main/resources/static/article/index.html");
        String readFile = getFileAsString(file);

        String userId = HttpSession.get(sessionId);
        String rendered = renderHeaderMenu(readFile,sessionId, userId);

        response.setBody(rendered.getBytes());
    }


    /**
     * 게시글 작성 요청 처리 핸들러.
     * 사용자가 로그인되지 않은 상태인 경우, 로그인 페이지로 리다이렉션시킴
     * @param request
     * @param response
     */
    @Mapping(path = "/article", method = HttpMethod.POST)
    public void createPost(HttpRequest request, HttpResponse response){

        response.setHeader("Content-Type", "text/html; charset=utf-8");

        Cookie cookie = request.getCookie();
        String sessionId = cookie.getCookie(SESSION_ID);

        String userId = HttpSession.get(sessionId);
        if (sessionId == null || userId == null) {
            response.setLocation("/login");
            return;
        }

        BoardDao boardDao = BoardDao.BOARDS;

        String contents = request.getParameter(CONTENTS);

        Board board = boardDao.save(new Board(0L, userId, contents, null)).orElseThrow(NoSuchElementException::new);
        response.setLocation("/board/" + board.getBoardId());
    }
}
