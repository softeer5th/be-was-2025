package http.handler;

import db.Database;
import db.SessionDB;
import http.enums.ErrorMessage;
import http.enums.HttpMethod;
import http.enums.HttpResponseStatus;
import http.request.HttpRequest;
import http.request.TargetInfo;
import http.response.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

public class PostHandler implements Handler {
    private static final String REDIRECT_MAIN_HTML = "/index.html";
    private static final String PHOTO_STORAGE_PATH = "src/main/resources/images/";

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
        String requestBody = request.getBody();
        logger.info("Boundary: {}", boundary);

        try {
            String sid = HttpRequestUtil.getCookieValueByKey(request, "sid");
            User user = SessionDB.getUser(sid);
            String userId = user.getUserId();
            String[] tokens = requestBody.split("\r\n");
            logger.debug("tokens: {}", Arrays.toString(tokens));

            Iterator<String> iterator = Arrays.asList(tokens).iterator();
            StringBuilder contentBuilder = new StringBuilder();
            StringBuilder photoBuilder = new StringBuilder();
            String filePath = null;

            iterator.next();
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (line.contains("name=\"photo\"")) {
                    String filename = Integer.toString(Database.getArticleCount());
                    line = iterator.next();
                    if (line.toLowerCase().startsWith("content-type: image/")) {
                        iterator.next();
                        while (iterator.hasNext() && !(line = iterator.next()).startsWith("--" + boundary)) {
                            photoBuilder.append(line);
                        }
                        File photoFile = new File(PHOTO_STORAGE_PATH + filename + ".png");
                        filePath = photoFile.getAbsolutePath();
                        try (FileOutputStream fos = new FileOutputStream(photoFile)) {
                            fos.write(photoBuilder.toString().getBytes("UTF-8"));
                        }
                    }
                } else if (line.contains("name=\"content\"")) {
                    iterator.next();
                    while (iterator.hasNext() && !(line = iterator.next()).startsWith("--" + boundary)) {
                        contentBuilder.append(line);
                    }
                }
            }

            logger.debug("contentBuilder: {}", contentBuilder);

            Database.addArticle(userId, contentBuilder.toString(), filePath);

            return builder
                    .redirectResponse(HttpResponseStatus.FOUND, REDIRECT_MAIN_HTML)
                    .build();

        } catch (Exception e) {
            logger.error(e.getMessage());
            return builder
                    .errorResponse(HttpResponseStatus.BAD_REQUEST, ErrorMessage.BAD_REQUEST)
                    .build();
        }
    }
}
