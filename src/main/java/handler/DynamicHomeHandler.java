package handler;

import enums.FileContentType;
import enums.HttpHeader;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import manager.CommentManager;
import manager.UserManager;
import model.Comment;
import model.Post;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.FileReader;
import util.HttpRequestParser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static enums.HttpStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.TimeFormatter.formatter;

/**
 * 로그인 여부에 따라 동적 HTML 파일을 반환하는 핸들러 클래스.
 * 이 클래스는 사용자가 로그인한 상태에 따라 동적으로 HTML을 생성하고,
 * 게시글 및 댓글을 표시하는 기능을 제공합니다. 또한 페이지 네비게이션을 위한 링크를 생성합니다.
 */
public class DynamicHomeHandler implements Handler {

    private static final Logger log = LoggerFactory.getLogger(DynamicHomeHandler.class);
    private static final int DEFAULT_PAGE = 1;

    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    private static final String USER_REPLACE_TARGET = "<!--user-->";
    private static final String POST_REPLACE_TARGET = "<!--post-->";
    private static final String PREV_REPLACE_TARGET = "<!--prev-->";
    private static final String NEXT_REPLACE_TARGET = "<!--next-->";
    private static final String NAV_TAG = "<a class=\"nav__menu__item__btn\" href=\"/index.html?page=%s\">";

    private final UserManager userManager;
    private final BoardManager boardManager;
    private final CommentManager commentManager;

    /**
     * 생성자. UserManager, BoardManager, CommentManager의 인스턴스를 초기화합니다.
     */
    public DynamicHomeHandler() {
        this.userManager = UserManager.getInstance();
        this.boardManager = BoardManager.getInstance();
        this.commentManager = CommentManager.getInstance();
    }

    /**
     * HTTP 요청을 처리하고 동적 HTML을 반환하는 메서드.
     * 요청된 경로에 따라 로그인 여부를 확인하고, 로그인된 사용자는 마이페이지 링크와 게시글을 표시,
     * 로그인하지 않은 사용자는 로그인 링크를 표시합니다.
     *
     * @param request HTTP 요청 정보 객체
     * @return 처리된 HTTP 응답
     */
    @Override
    public HttpResponse handle(HttpRequestInfo request) {
        log.debug("request : {}", request);
        String path = request.getPath(); // index.html?page=1
        final int totalPage = boardManager.getPageSize();
        final int page = getPage(path, totalPage);

        HttpResponse response = new HttpResponse();

        String html = FileReader.readFileAsString(STATIC_FILE_PATH + "/index.html")
                .orElseThrow(() -> new ClientErrorException(ErrorCode.FILE_NOT_FOUND));

        final String sessionId = CookieParser.parseCookie(request.getHeaderValue(HttpHeader.COOKIE.getName()));
        final Optional<User> user = userManager.getUserFromSession(sessionId);

        StringBuilder dynamicHtmlContent = new StringBuilder();

        startHeaderMenu(dynamicHtmlContent);
        user.ifPresentOrElse(
                (loginUser) -> addUserNameToHtml(loginUser.getName(), dynamicHtmlContent),
                () -> addAuthLinksToHtml(dynamicHtmlContent)
        );
        endHeaderMenu(dynamicHtmlContent);

        String body = html.replace(USER_REPLACE_TARGET, dynamicHtmlContent.toString());
        StringBuilder postContent = new StringBuilder();
        final Post post = boardManager.getPostByPage(page);
        addPost(post, postContent, user);

        body = body.replace(POST_REPLACE_TARGET, postContent.toString());
        body = body.replace(PREV_REPLACE_TARGET, String.format(NAV_TAG, Math.max(1, page - 1)));
        body = body.replace(NEXT_REPLACE_TARGET, String.format(NAV_TAG, Math.min(totalPage, page + 1)));

        response.setResponse(OK, FileContentType.HTML_UTF_8, body);
        return response;
    }

    /**
     * 요청된 경로에서 페이지 번호를 추출하고, 해당 페이지 번호를 반환합니다.
     * 페이지 번호는 요청 파라미터에서 확인하며, 범위를 벗어난 경우 기본 페이지 번호인 1을 반환합니다.
     *
     * @param path      요청된 경로
     * @param totalPage 전체 페이지 수
     * @return 요청된 페이지 번호
     */
    private int getPage(String path, int totalPage) {
        final String[] split = path.split("\\?");
        if (split.length == 2) {
            final Map<String, String> param = HttpRequestParser.parseParamString(split[1]);
            int page = Integer.parseInt(param.get("page"));
            page = Math.max(1, page);
            return Math.min(page, totalPage);
        }
        return DEFAULT_PAGE;
    }

    /**
     * 게시글 정보를 HTML 콘텐츠로 추가하는 메서드.
     * 게시글의 작성자, 작성일자, 본문 내용 및 댓글을 HTML로 추가합니다.
     * 로그인한 사용자에게는 좋아요, 북마크 및 댓글 작성 폼도 표시됩니다.
     *
     * @param post        게시글 객체
     * @param postContent HTML 콘텐츠를 추가할 StringBuilder
     * @param user        로그인한 사용자 정보 (없으면 비어있음)
     */
    private void addPost(Post post, StringBuilder postContent, Optional<User> user) {
        if (post == null) {
            postContent.append("<h2> 글이 없어요 ㅜㅜ </h2>");
            return;
        }
        final User author = userManager.getUserOrElseThrow(post.getAuthor());
        postContent
                .append("""
                           <div class="wrapper">
                        <div class="post">
                          <div class="post__account">
                        """)
                .append(addProfileImage(author.getProfile()))
                .append("""
                            <p class="post__account__nickname">
                        """)
                .append(URLDecoder.decode(author.getName(), UTF_8))
                .append("""
                        </p>
                        <p class="post__createdAt">""")
                .append(post.getCreatedAt().format(formatter()))
                .append("""
                        </p>
                        </div>
                        """)
                .append(addImage(post.getFile()))
                .append("""
                        <div class="post__menu">
                          <ul class="post__menu__personal">
                            <li>
                        """)
                .append(addLikeSvg(post.getId(), user))
                .append("""
                        </li>
                        <li>
                        """)
                .append(addMailHrefByUser(user))
                .append("""         
                          <button class="post__menu__btn">
                            <img src="./img/sendLink.svg" alt="Send Email" />
                          </button>
                        </a>
                        </li>
                        </ul>
                        """)
                .append(addBookMarkSvg(post.getId(), user))
                .append("""
                        </div>
                        <p class="post__article">
                        """)
                .append(post.getContents())
                .append("""
                                  </p>
                                </div>
                        """);
        // 댓글
        final List<Comment> comments = commentManager.getCommentsByPostId(post.getId());
        postContent.append("<ul class=\"comment\">");
        addComments(postContent, comments);

        if (user.isEmpty())
            postContent.append("</ul>");
            // 로그인 시 댓글 작성칸 추가
        else {
            postContent.append("""
                              <li class = "comment-form">
                            <h3>댓글 작성하기</h3>
                            <form action="/comment/write/
                            """)
                    .append(post.getId())
                    .append(
                            """
                                                 "  method="POST">
                                              <textarea name="comment" id="comment" placeholder="댓글을 입력하세요..." rows="4" required></textarea>
                                                 <button  type="submit" class="btn btn_ghost btn_size_m btn btn_primary btn_size_m">댓글 작성</button>     
                                             </form>
                                         </li>
                                    </ul>
                                    """);
        }
    }

    private String addProfileImageForComment(String file) {
        if (file == null)
            return "<img class = \"comment__item__user__img\" src = \"/img/default.png\" /> ";
        return String.format("<img class = \"comment__item__user__img\" src = \"/img/%s\" />", file);
    }


    private String addProfileImage(String file) {
        if (file == null)
            return "<img class = \"post__account__img\" src = \"/img/default.png\" /> ";
        return String.format("<img class = \"post__account__img\" src = \"/img/%s\" />", file);
    }

    private String addImage(String file) {
        if (file == null)
            return "<img class = \"post__img\" src = \"/img/default.png\" /> ";
        return String.format("<img class = \"post__img\" src = \"/img/%s\" />", file);
    }

    /**
     * 주어진 댓글 리스트를 HTML로 변환하여 StringBuilder에 추가합니다.
     *
     * @param postContent 댓글 리스트를 HTML로 추가할 StringBuilder
     * @param comments    댓글 목록
     */
    private void addComments(StringBuilder postContent, List<Comment> comments) {
        if (comments.isEmpty()) {
            postContent.append("<h2>댓글이 없습니다 ㅜㅜ</h2>");
            return;
        }
        for (Comment comment : comments) {
            final User commentAuthor = userManager.getUserOrElseThrow(comment.getAuthor());
            postContent.append("""
                            <li class="comment__item">
                                       <div class="comment__item__user">
                            """)
                    .append(addProfileImageForComment(commentAuthor.getProfile()))
                    .append("""
                                           <p class="comment__item__user__nickname">
                            """)
                    .append(URLDecoder.decode(commentAuthor.getName(), UTF_8))
                    .append("""
                            </p>
                            """)
                    .append(comment.getCreatedAt().format(formatter()))
                    .append("""
                            </div>
                            <p class="comment__item__article">
                            """)

                    .append(comment.getContents())
                    .append("""
                                    </p>
                                    </li>
                            """);
        }
    }

    private final String likeSvg = """
             <form action="/board/like/%s" method="POST">
             <button type="submit" class="post__menu__btn">
             <img src="./img/like.svg" alt="Like" />
             </button>
             </form>
            """;
    private final String likedSvg = """
            <form action="/board/like/%s" method="POST">
            <button type="submit" class="post__menu__btn">
            <img src="./img/liked.svg" alt="Like" />
            </button>
            </form>
            """;

    /**
     * 로그인한 사용자에 맞는 좋아요 버튼을 HTML로 반환합니다.
     *
     * @param postId 게시글 ID
     * @param user   로그인한 사용자 정보 (없으면 비어있음)
     * @return 좋아요 버튼 HTML
     */
    private String addLikeSvg(int postId, Optional<User> user) {
        if (user.isEmpty())
            return String.format(likeSvg, postId);
        if (!boardManager.existsPostLike(postId, user.get().getId()))
            return String.format(likeSvg, postId);

        return String.format(likedSvg, postId);
    }

    private final String bookmarkSvg = """
              <form action="/board/mark/%s" method="POST">
                            <button type="submit" class="post__menu__btn">
                            <img src="./img/bookMark.svg" alt="bookmark" />
                            </button>
                            </form>
            """;
    private final String bookmarkedSvg = """
              <form action="/board/mark/%s" method="POST">
                            <button type="submit" class="post__menu__btn">
                            <img src="./img/bookMarked.svg" alt="bookmark" />
                            </button>
                            </form>
            """;

    private static final String BLANK = ""; // 빈 문자열을 나타내는 상수

    /**
     * 로그인한 사용자가 있을 경우 이메일 전송을 위한 링크를 HTML로 생성합니다.
     * 사용자가 로그인하지 않으면 빈 문자열을 반환합니다.
     *
     * @param user 로그인한 사용자 정보 (없으면 빈 Optional)
     * @return 로그인한 경우 이메일 링크 HTML, 로그인하지 않은 경우 빈 문자열
     */
    private String addMailHrefByUser(Optional<User> user) {
        if (user.isPresent())
            return """
                    <a href="mailto:example@example.com">
                    """;
        return BLANK; // 로그인하지 않은 경우 빈 문자열 반환
    }

    /**
     * 로그인한 사용자에 맞는 북마크 버튼을 HTML로 반환합니다.
     *
     * @param postId 게시글 ID
     * @param user   로그인한 사용자 정보 (없으면 비어있음)
     * @return 북마크 버튼 HTML
     */
    private String addBookMarkSvg(int postId, Optional<User> user) {
        if (user.isEmpty())
            return String.format(bookmarkSvg, postId);
        if (!boardManager.existsPostBookMark(postId, user.get().getId()))
            return String.format(bookmarkSvg, postId);

        return String.format(bookmarkedSvg, postId);
    }

    /**
     * HTML 헤더 메뉴를 시작하는 HTML 태그를 추가합니다.
     *
     * @param dynamicHtmlContent 동적 HTML 콘텐츠를 추가할 StringBuilder
     */
    private void startHeaderMenu(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("<ul class=\"header__menu\">")
                .append("<li class=\"header__menu__item\">");

    }

    /**
     * HTML 헤더 메뉴를 종료하는 HTML 태그를 추가합니다.
     *
     * @param dynamicHtmlContent 동적 HTML 콘텐츠를 추가할 StringBuilder
     */
    private void endHeaderMenu(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("</li>")
                .append("</ul>");
    }

    /**
     * 로그인하지 않은 사용자를 위해 로그인 및 회원 가입 링크를 추가합니다.
     *
     * @param dynamicHtmlContent 동적 HTML 콘텐츠를 추가할 StringBuilder
     */
    private void addAuthLinksToHtml(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("<a class=\"btn btn_contained btn_size_s\" href=\"/login/index.html\">로그인</a>")
                .append("</li>")
                .append("<li class=\"header__menu__item\">")
                .append("<a class=\"btn btn_ghost btn_size_s\" href = \"/registration/index.html\" >")
                .append("회원 가입")
                .append("</a>");
    }

    /**
     * 로그인한 사용자의 이름을 HTML로 추가합니다.
     *
     * @param name               사용자 이름
     * @param dynamicHtmlContent 동적 HTML 콘텐츠를 추가할 StringBuilder
     */
    private void addUserNameToHtml(String name, StringBuilder dynamicHtmlContent) {
        name = URLDecoder.decode(name, UTF_8);
        dynamicHtmlContent
                .append("<a class=\"my-page-link\" href=\"/mypage/index.html\">")
                .append(name)
                .append("님</a>")
                .append("<a class=\"write-article-link\" href=\"/article/index.html\"> 글쓰기 </a>")
                .append("<a class=\"logout-link\" href=\"/user/logout\"> 로그아웃 </a>");
    }
}