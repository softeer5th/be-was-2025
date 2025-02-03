package http.handler;

import db.Database;
import db.SessionDB;
import http.enums.ErrorMessage;
import http.enums.HttpMethod;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.HttpRequestParser;
import http.request.TargetInfo;
import http.response.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PostHandler implements Handler {
    private static final String REDIRECT_MAIN_HTML = "/index.html";
    private static final String PHOTO_STORAGE_PATH = "src/main/resources/static/images/";

    private static final PostHandler instance = new PostHandler();

    private static final Logger logger = LoggerFactory.getLogger(PostHandler.class);

    private PostHandler() {}

    public static PostHandler getInstance() {
        return instance;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        HttpResponse.Builder builder = new HttpResponse.Builder();
        TargetInfo target = request.getTarget();
        String path = target.getPath();

        if (path.equals("/post/article")) {
            return handleAddArticle(request, builder);
        } else if (path.equals("/post/comment")) {
            return handleAddComment(request, builder);
        } else {
            builder.errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST);
        }

        return builder.build();
    }

    private boolean checkValidHttpMethod(HttpMethod method, HttpRequest request) {
        return method.equals(request.getMethod());
    }

    private HttpResponse handleAddArticle(HttpRequest request, HttpResponse.Builder builder) throws IOException {
        if (!checkValidHttpMethod(HttpMethod.POST, request)) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST)
                    .build();
        }

        String boundary = HttpRequestUtil.getBoundary(request);
        byte[] requestBody = request.getBody();
        logger.info("Boundary: {}", boundary);

        try {
            String sid = HttpRequestUtil.getCookieValueByKey(request, "sid");
            User user = SessionDB.getUser(sid);
            String userId = user.getUserId();

            String boundaryLine = "--" + boundary;
            int boundaryLength = boundaryLine.length();
            int index = 0;

            String content = null;
            byte[] photoBytes = null;

            while (index < requestBody.length) {
                int boundaryStart = findBoundary(requestBody, boundaryLine.getBytes(), index);
                if (boundaryStart == -1) break;
                index = boundaryStart + boundaryLength;

                int headersEnd = findBoundary(requestBody, "\r\n\r\n".getBytes(), index);
                if (headersEnd == -1) break;
                String headers = new String(requestBody, index, headersEnd - index, "UTF-8");
                index = headersEnd + 4; // CRLF 넘기기

                if (headers.contains("name=\"photo\"")) {
                    int dataEnd = findBoundary(requestBody, boundaryLine.getBytes(), index);
                    if (dataEnd == -1) break;
                    photoBytes = Arrays.copyOfRange(requestBody, index, dataEnd - 2); // CRLF는 제거
                    index = dataEnd;
                } else if (headers.contains("name=\"content\"")) {
                    int dataEnd = findBoundary(requestBody, boundaryLine.getBytes(), index);
                    if (dataEnd == -1) break;
                    content = new String(requestBody, index, dataEnd - index - 2, "UTF-8"); // CRLF는 제거
                    index = dataEnd;
                }
            }

            String filePath = null;
            String fileName = null;
            if (photoBytes != null) {
                fileName = UUID.randomUUID() + ".jpg";
                File photoFile = new File(PHOTO_STORAGE_PATH + fileName);
                try (FileOutputStream fos = new FileOutputStream(photoFile)) {
                    fos.write(photoBytes);
                }
            }

            Database.addArticle(userId, content, "images/" + fileName);

            return builder
                    .redirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML)
                    .build();

        } catch (Exception e) {
            logger.error("Error processing article: {}", e.getMessage());
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST)
                    .build();
        }
    }

    private HttpResponse handleAddComment(HttpRequest request, HttpResponse.Builder builder) throws IOException {
        if (!checkValidHttpMethod(HttpMethod.POST, request)) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST)
                    .build();
        }

        String sid = HttpRequestUtil.getCookieValueByKey(request, "sid");
        User user = SessionDB.getUser(sid);
        Map<String, Object> params = HttpRequestParser.parseRequestBody(new String(request.getBody(), "UTF-8"));
        String articleId = getParam(params, "articleId").map(Object::toString).orElse(null);
        String content = getParam(params, "content").map(Object::toString).orElse(null);
        String userId = user.getUserId();

        logger.debug("ArticleId: {}, User ID: {}, Content: {}", articleId, userId, content);

        if (articleId == null || articleId.isEmpty() || userId == null || userId.isEmpty()) {
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.INVALID_PARAMETER)
                    .build();
        }

        Database.addComment(userId, Integer.parseInt(articleId), content);


        return builder
                .redirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML)
                .build();
    }

    private int findBoundary(byte[] data, byte[] boundary, int start) {
        for (int i = start; i <= data.length - boundary.length; i++) {
            boolean match = true;
            for (int j = 0; j < boundary.length; j++) {
                if (data[i + j] != boundary[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
        }
        return -1;
    }

    private Optional<Object> getParam(Map<String, Object> params, String key) {
        return Optional.ofNullable(params.get(key));
    }
}
