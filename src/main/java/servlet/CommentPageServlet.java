package servlet;

import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatus;
import webserver.http.servlet.HttpServlet;
import webserver.session.HttpSession;

public class CommentPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        StringBuilder htmlBuilder = new StringBuilder();
        HttpSession httpSession = request.getSession(false);
        if(httpSession == null || httpSession.getAttribute("user") == null) {
            response.sendRedirect("/login/index.html");
            return;
        }
        String index = request.getParameter("index");
        String articleId = request.getParameter("articleId");

        htmlBuilder.append("""
                <!DOCTYPE html>
                <html>
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <link href="../reset.css" rel="stylesheet" />
                    <link href="../global.css" rel="stylesheet" />
                  </head>
                  <body>
                    <div class="container">
                      <header class="header">
                        <a href="/"><img src="../img/signiture.svg" /></a>
                        <ul class="header__menu">
                          <li class="header__menu__item">
                            <form action="/api/logout" method="post" style="margin: 0;">
                              <button class="btn btn_ghost btn_size_s" type="submit">로그아웃</button>
                            </form>
                          </li>
                        </ul>
                      </header>
                      <div class="page">
                        <h2 class="page-title">댓글 작성</h2>
                        <form class="form" method="post" action="/api/comments?articleId=
                        """);
        htmlBuilder.append(articleId);
        htmlBuilder.append("&index=");
        htmlBuilder.append(index);
        htmlBuilder.append("""
                         ">
                          <div class="textfield textfield_size_m">
                            <p class="title_textfield">내용</p>
                            <textarea
                              class="input_textfield"
                              name="content"
                              placeholder="글의 내용을 입력하세요"
                              autocomplete="username"
                            ></textarea>
                          </div>
                          <button
                            id="registration-btn"
                            class="btn btn_contained btn_size_m"
                            style="margin-top: 24px"
                            type="submit"
                          >
                            작성 완료
                          </button>
                        </form>
                      </div>
                    </div>
                  </body>
                </html>
                """);

        response.setBody(htmlBuilder.toString());
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpStatus.OK);
    }
}
