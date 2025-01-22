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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static enums.HttpStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.TimeFormatter.formatter;

/*
 * 로그인 여부에 따라 동적 html 파일을 반환하는 핸들러 클래스
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

    public DynamicHomeHandler() {
        this.userManager = UserManager.getInstance();
        this.boardManager = BoardManager.getInstance();
        this.commentManager = CommentManager.getInstance();
    }


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

    private void addPost(Post post, StringBuilder postContent, Optional<User> user) {
        postContent
                .append("""
                           <div class="wrapper">
                        <div class="post">
                          <div class="post__account">
                            <img class="post__account__img" src = "/img/img.png"/>
                            <p class="post__account__nickname">
                        """)
                .append(post.getAuthor())
                .append("""
                        </p>
                        <p class="post__createdAt">""")
                .append(post.getCreatedAt().format(formatter()))
                .append("""
                        </p>
                        </div>
                        <img class="post__img" />
                        <div class="post__menu">
                          <ul class="post__menu__personal">
                            <li>
                        """)
                .append(addLikeSvg(post.getId(), user))
                .append("""
                        </li>
                        <li>
                          <a href="mailto:example@example.com">
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

    private static void addComments(StringBuilder postContent, List<Comment> comments) {
        for (Comment comment : comments) {
            postContent.append("""
                            <li class="comment__item">
                                       <div class="comment__item__user">
                                           <img class="comment__item__user__img"/>
                                           <p class="comment__item__user__nickname">
                            """)
                    .append(comment.getAuthor())
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

    private String addBookMarkSvg(int postId, Optional<User> user) {
        if (user.isEmpty())
            return String.format(bookmarkSvg, postId);
        if (!boardManager.existsPostBookMark(postId, user.get().getId()))
            return String.format(bookmarkSvg, postId);

        return String.format(bookmarkedSvg, postId);
    }

    private void startHeaderMenu(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("<ul class=\"header__menu\">")
                .append("<li class=\"header__menu__item\">");

    }

    private void endHeaderMenu(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("</li>")
                .append("</ul>");
    }

    private void addAuthLinksToHtml(StringBuilder dynamicHtmlContent) {
        dynamicHtmlContent
                .append("<a class=\"btn btn_contained btn_size_s\" href=\"/login/index.html\">로그인</a>")
                .append("</li>")
                .append("<li class=\"header__menu__item\">")
                .append("<a class=\"btn btn_ghost btn_size_s\" href = \"/registration/index.html\" >")
                .append("회원 가입")
                .append("</a>");
    }

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
