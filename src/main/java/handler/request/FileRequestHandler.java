package handler.request;

import http.cookie.Cookie;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.enums.MimeType;
import http.session.SessionManager;
import provider.DynamicDataProvider;
import provider.HomeDataProvider;
import provider.Model;
import provider.MyPageDataProvider;
import template.TemplateEngine;
import util.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileRequestHandler implements RequestHandler{
    private final Map<String, DynamicDataProvider> dynamicDataProviderMap = new HashMap<>();
    private final List<String> restrictedPathsForUser = Arrays.asList("/mypage", "/write");
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final TemplateEngine templateEngine = TemplateEngine.getInstance();

    public FileRequestHandler(){
        dynamicDataProviderMap.put("/", new HomeDataProvider());
        dynamicDataProviderMap.put("/mypage", new MyPageDataProvider());
    }

    @Override
    public boolean canHandle(HttpRequest httpRequest) {
        if(httpRequest.getMethod() == HttpMethod.GET && FileUtil.isFileExist(httpRequest.getPath())){
            return true;
        }
        return false;
    }

    @Override
    public HttpResponse handle(HttpRequest httpRequest) {
        // 로그인한 사용자만 접근할 수 있는 페이지에 접근 시 쿠키 검사, 쿠키가 없다면 메인 페이지로 redirect
        if(restrictedPathsForUser.contains(httpRequest.getPath())){
            if(httpRequest.getCookie("sessionId") == null){
                return new HttpResponse.Builder()
                        .httpStatus(HttpStatus.FOUND)
                        .location("http://localhost:8080")
                        .build();
            }
        }

        File file = FileUtil.getFile(httpRequest.getPath());

        // 파일 확장자 -> Content-Type
        String extension = FileUtil.extractFileExtension(file.getPath());

        // 파일 읽기
        byte[] fileData = FileUtil.readFileToByteArray(file);

        if(fileData == null){
            byte[] messageData = "파일 읽는 과정에서 예외가 발생했습니다".getBytes();

            return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MimeType.TEXT_PLAIN.getMimeType())
                    .body(messageData)
                    .build();
        }

        DynamicDataProvider dynamicDataProvider = dynamicDataProviderMap.get(httpRequest.getPath());

        if(dynamicDataProvider != null){
            Map<String, Object> params = new HashMap<>();

            Cookie cookie = httpRequest.getCookie("sessionId");

            if(cookie != null){
                Long userId = (Long)sessionManager.getSessionAttribute(cookie.getValue(), "userId");
                params.put("userId", userId);
            }

            params.putAll(httpRequest.getQueryParams());

            Model model = dynamicDataProvider.provideData(params);
            String htmlContent = templateEngine.renderTemplate(fileData, model);
            fileData = htmlContent.getBytes();
        }

        return new HttpResponse.Builder()
                    .httpStatus(HttpStatus.OK)
                    .contentType(MimeType.getMimeType(extension))
                    .body(fileData)
                    .build();
    }
}
