package webserver.request.api;

import db.Database;
import model.Article;
import model.ContentType;
import model.Cookie;
import webserver.HTTPExceptions;
import webserver.request.HTTPRequestBody;
import webserver.request.HTTPRequestHeader;
import webserver.request.RequestProcessor;
import webserver.response.HTTPResponse;
import webserver.response.HTTPResponseBody;
import webserver.response.HTTPResponseHeader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LatestArticleApiHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, String queryParams, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;
        int articleId = -1;
        String authorName = "", content = "";

        try {
            Article article = Database.getLatestArticle();

            if (article != null) {
                articleId = article.getId();
                authorName = article.getAuthorName();
                content = article.getContent();
            }

            String jsonResponse =
                    "{\"articleId\": " + articleId +
                            ", \"authorName\": \"" + authorName +
                            "\", \"content\": \"" + content + "\"}";
            responseBody = new HTTPResponseBody(jsonResponse.getBytes(StandardCharsets.UTF_8));

            responseHeader.setStatusCode(200);
            responseHeader.addHeader("Content-Type", ContentType.getContentType(".json"));
        } catch (HTTPExceptions e) {
            responseHeader.setStatusCode(e.getStatusCode());
            responseBody = new HTTPResponseBody(HTTPExceptions.getErrorMessageToBytes(e.getMessage()));
        }

        for (Cookie cookie : cookieList) {
            responseHeader.addHeader("Set-Cookie", cookie.toString());
        }

        return new HTTPResponse(responseHeader, responseBody);
    }
}
