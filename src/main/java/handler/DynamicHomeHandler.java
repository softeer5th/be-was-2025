package handler;

import enums.FileContentType;
import enums.HttpHeader;
import exception.ClientErrorException;
import exception.ErrorCode;
import manager.BoardManager;
import manager.UserManager;
import model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HttpRequestInfo;
import response.HttpResponse;
import util.CookieParser;
import util.FileReader;

import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import static enums.HttpStatus.OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static util.TimeFormatter.formatter;

/*
 * 로그인 여부에 따라 동적 html 파일을 반환하는 핸들러 클래스
 */
public class DynamicHomeHandler implements Handler {
    private static final Logger log = LoggerFactory.getLogger(DynamicHomeHandler.class);

    private static final String STATIC_FILE_PATH = System.getenv("STATIC_FILE_PATH");
    private static final String USER_REPLACE_TARGET = "<!--user-->";
    private static final String POST_REPLACE_TARGET = "<!--post-->";

    private final UserManager userManager;
    private final BoardManager boardManager;

    public DynamicHomeHandler() {
        this.userManager = UserManager.getInstance();
        this.boardManager = BoardManager.getInstance();
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
        final Optional<String> userName = userManager.getNameFromSession(sessionId);

        StringBuilder dynamicHtmlContent = new StringBuilder();

        startHeaderMenu(dynamicHtmlContent);
        userName.ifPresentOrElse(
                name -> addUserNameToHtml(name, dynamicHtmlContent),
                () -> addAuthLinksToHtml(dynamicHtmlContent)
        );
        endHeaderMenu(dynamicHtmlContent);

        String body = html.replace(USER_REPLACE_TARGET, dynamicHtmlContent.toString());
        StringBuilder postContent = new StringBuilder();
        final List<Post> posts = boardManager.getPosts();
        addPosts(posts, postContent);
        body = body.replace(POST_REPLACE_TARGET, postContent.toString());

        response.setResponse(OK, extension, body);
        return response;
    }

    private void addPosts(List<Post> posts, StringBuilder postContent) {
        for (Post post : posts) {

            postContent
                    .append("""
                               <div class="wrapper">
                            <div class="post">
                              <div class="post__account">
                                <img class="post__account__img" />
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
                               <form action="/board/like/
                            """)
                    .append(post.getId())
                    .append(
                            """
                                    " method="POST">
                                                       <button type="submit" class="post__menu__btn">
                                                         <img src="./img/like.svg" alt="Like" />
                                                       </button>
                                                     </form>
                                          </li>
                                          <li>
                                            <button class="post__menu__btn">
                                              <img src="./img/sendLink.svg" />
                                            </button>
                                          </li>
                                        </ul>
                                        <button class="post__menu__btn">
                                          <img src="./img/bookMark.svg" />
                                        </button>
                                      </div>
                                      <p class="post__article">
                                    """)
                    .append(post.getContents())
                    .append("""
                                      </p>
                                    </div>
                            """);

        }
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
                .append("<a class=\"btn btn_contained btn_size_s\" href=\"/article/index.html\"> 글쓰기 </a>")
                .append("<a class=\"btn btn_contained btn_size_s\" href=\"/mypage/index.html\">")
                .append(name)
                .append("님</a>")
                .append("<a href=\"/user/logout\"> 로그아웃 </a>");
    }
}
