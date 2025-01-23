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

public class NextArticleApiHandler implements RequestProcessor {
    @Override
    public HTTPResponse handle(HTTPRequestHeader requestHeader, HTTPRequestBody requestBody, String queryParams, HTTPResponseHeader responseHeader, List<Cookie> cookieList) throws IOException {
        HTTPResponseBody responseBody;
        int articleId = -1;
        String authorName = "", content = "";

        try {
            int currentArticleId = parseParameter(queryParams);

            Article article = Database.getNextArticle(currentArticleId);

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

    public static int parseParameter(String queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            throw new HTTPExceptions.Error400("Query parameter is empty");
        }

        // Check if the queryParams matches the expected format
        String expectedKey = "currentArticleId=";
        if (!queryParams.startsWith(expectedKey)) {
            throw new HTTPExceptions.Error400("Invalid query parameter format. Expected format: 'currentArticleId=?'");
        }

        String valuePart = queryParams.substring(expectedKey.length());

        try {
            return Integer.parseInt(valuePart);
        } catch (NumberFormatException e) {
            throw new HTTPExceptions.Error400("Invalid value for 'currentArticleId'. It must be an integer.");
        }
    }
}
