package servlet;

import db.h2.ArticleStorage;
import db.h2.CommentStorage;
import model.Article;
import model.Comment;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

import java.util.List;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        StringBuilder htmlBuilder = new StringBuilder();
        HttpSession session = request.getSession(false);
        ArticleStorage articleStorage = ArticleStorage.getInstance();
        List<Article> articles = articleStorage.findAll();
        int currentIndex = request.getParameter("index") != null ? Integer.parseInt(request.getParameter("index")) : 0;

        htmlBuilder.append("""
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <link href="./reset.css" rel="stylesheet" />
              <link href="./global.css" rel="stylesheet" />
              <link href="./main.css" rel="stylesheet" />
              <title>홈</title>
            </head>
            <body>
            <div class="container">
              <header class="header">
                <a href="/"><img src="./img/signiture.svg" /></a>
                <ul class="header__menu">
        """);

        User user = null;
        if (session != null) user = (User) session.getAttribute("user");

        if (user != null) {
            htmlBuilder.append("""
            <li class="header__menu__item" style="display: flex; gap: 8px; align-items: center;">
              <a class="btn btn_contained btn_size_s" href="/mypage">""")
                    .append(user.getName())
                    .append("의 마이페이지")
                    .append("""
              </a>
              <form action="/api/logout" method="POST" style="margin: 0;">
                <button class="btn btn_ghost btn_size_s" type="submit">로그아웃</button>
              </form>
            </li>
            """);
        } else {
            htmlBuilder.append("""
            <li class="header__menu__item">
              <a class="btn btn_contained btn_size_s" href="/login/index.html">로그인</a>
            </li>
            <li class="header__menu__item">
              <a class="btn btn_ghost btn_size_s" href="/registration/index.html">회원 가입</a>
            </li>
            """);
        }
        java.lang.String account = articles.size() <= currentIndex ? "" : articles.get(currentIndex).getUser().getName();

        htmlBuilder.append("""
                </ul>
              </header>
              <div class="wrapper">
                <div class="post">
                  <div class="post__account">
                    <img class="post__account__img" />
                    <p class="post__account__nickname">""")
                .append(account)
                .append("""
                  </p>
                  </div>
                  <img class="post__img" />
                  <div class="post__menu">
                    <ul class="post__menu__personal">
                      <li>
                        <button class="post__menu__btn">
                          <img src="./img/like.svg" />
                        </button>
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
                  """);


        if (!articles.isEmpty() && currentIndex < articles.size()) {
            Article article = articles.get(currentIndex);
            String content = article.getContent().replaceAll("\r\n|\r|\n", "<br>");
            htmlBuilder.append(content);
            CommentStorage commentStorage = CommentStorage.getInstance();
            List<Comment> comments = commentStorage.findCommentsByArticle(article);
            htmlBuilder.append("""
                             <ul class="comment">
                             """);
            for (Comment comment : comments) {
                htmlBuilder.append("""
                      <li class="comment__item">
                        <div class="comment__item__user">
                          <img class="comment__item__user__img" />
                          <p class="comment__item__user__nickname">""");
                htmlBuilder.append(comment.getUser().getName());
                htmlBuilder.append("""
                        </p>
                        </div>
                        <p class="comment__item__article">
                        """);
                htmlBuilder.append(comment.getContent().replaceAll("\r\n|\r|\n", "<br>"));
                htmlBuilder.append("""
                        </p>
                      </li>
                       """);
            }
            htmlBuilder.append("""
                    </ul>
                    """);
        }

        boolean hasPreviousArticle = currentIndex > 0;
        boolean hasNextArticle = currentIndex < articles.size() - 1;

        htmlBuilder.append("""
        </p>
        </div>
        <nav class="nav">
          <ul class="nav__menu">
        """);

        if (hasPreviousArticle) {
            htmlBuilder.append("""
            <li class="nav__menu__item">
              <a class="nav__menu__item__btn" href="/?index=""")
                    .append(currentIndex - 1)
                    .append("""
                ">
                <img class="nav__menu__item__img" src="./img/ci_chevron-left.svg" />
                이전 글
              </a>
            </li>
            """);
        } else {
            htmlBuilder.append("""
            <li class="nav__menu__item">
              <span class="nav__menu__item__btn disabled">
                <img class="nav__menu__item__img" src="./img/ci_chevron-left.svg" />
                이전 글
              </span>
            </li>
            """);
        }
        if(articles.size() > currentIndex) {
            htmlBuilder.append("""
                    <li class="nav__menu__item">
                      <a class="btn btn_ghost btn_size_m" href="/comments?index=""");
            htmlBuilder.append(currentIndex);
            htmlBuilder.append("&articleId=");
            htmlBuilder.append(articles.get(currentIndex).getId());
            htmlBuilder.append("""
                    ">
                            댓글 작성
                          </a>
                        </li>
                        <li class="nav__menu__item">
                          <a class="btn btn_ghost btn_size_m" href="/article">
                             글쓰기
                          </a>
                        </li>
                    """);
        } else {
            htmlBuilder.append("""
                    <li class="nav__menu__item">
                    <span class="btn btn_ghost btn_size_m disabled" style="pointer-events: none; cursor: default;">
                           댓글 작성
                          </span>
                        </li>
                        <li class="nav__menu__item">
                          <a class="btn btn_ghost btn_size_m" href="/article">
                             글쓰기
                          </a>
                        </li>
                    """);
        }

        if (hasNextArticle) {
            htmlBuilder.append("""
            <li class="nav__menu__item">
              <a class="nav__menu__item__btn" href="/?index=""")
                    .append(currentIndex + 1)
                    .append("""
                ">
                다음 글
                <img class="nav__menu__item__img" src="./img/ci_chevron-right.svg" />
              </a>
            </li>
            """);
        } else {
            htmlBuilder.append("""
            <li class="nav__menu__item">
              <span class="nav__menu__item__btn disabled">
                다음 글
                <img class="nav__menu__item__img" src="./img/ci_chevron-right.svg" />
              </span>
            </li>
            """);
        }

        htmlBuilder.append("""
          </ul>
        </nav>
        </div>
        </div>
        </body>
        </html>
        """);

        response.setStatus(HttpStatus.OK);
        response.setContentType("text/html");
        response.setBody(htmlBuilder.toString());
    }
}