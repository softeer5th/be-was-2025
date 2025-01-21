package servlet;

import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        StringBuilder htmlBuilder = new StringBuilder();
        HttpSession session = request.getSession(false);

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

        // Close Header
        htmlBuilder.append("""
                </ul>
              </header>
              <div class="wrapper">
                <div class="post">
                  <div class="post__account">
                    <img class="post__account__img" />
                    <p class="post__account__nickname">account</p>
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
                    우리는 시스템 아키텍처에 대한 일관성 있는 접근이 필요하며, 필요한 모든 측면은 이미 개별적으로 인식되고 있다고 생각합니다. 즉, 응답이
                    잘 되고, 탄력적이며 유연하고 메시지 기반으로 동작하는 시스템 입니다. 우리는 이것을 리액티브 시스템(Reactive Systems)라고 부릅니다.
                  </p>
                </div>
                <ul class="comment">
                  <li class="comment__item">
                    <div class="comment__item__user">
                      <img class="comment__item__user__img" />
                      <p class="comment__item__user__nickname">account</p>
                    </div>
                    <p class="comment__item__article">댓글 내용</p>
                  </li>
                </ul>
              </div>
            </div>
            </body>
            </html>
        """);

        // Write the HTML to the response
        response.setStatus(HttpStatus.OK);
        response.setContentType("text/html");
        response.setBody(htmlBuilder.toString());
    }
}
