package handler;

import domain.User;
import domain.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.enums.HttpStatusCode;
import webserver.handler.HttpHandler;
import webserver.request.FileUploader;
import webserver.request.HttpRequest;
import webserver.request.Multipart;
import webserver.response.HttpResponse;
import webserver.session.HttpSession;
import webserver.view.ModelAndTemplate;

import static enums.PageMappingPath.INDEX;

/**
 * 마이페이지 요청을 처리하는 핸들러
 */
public class MypageHandler implements HttpHandler {
    private static final String TEMPLATE_NAME = "/mypage/index.html";
    private static final Logger log = LoggerFactory.getLogger(MypageHandler.class);
    private final UserDao userDao;

    private final FileUploader uploader;

    public MypageHandler(UserDao userDao, FileUploader uploader) {
        this.userDao = userDao;
        this.uploader = uploader;
    }

    /**
     * 마이페이지를 보여준다
     */
    @Override
    public HttpResponse handleGet(HttpRequest request) {
        return HttpResponse.render(TEMPLATE_NAME);
    }

    /**
     * 마이페이지 정보를 수정한다
     */
    @Override
    public HttpResponse handlePost(HttpRequest request) {
        UserUpdateRequest body = parseRequest(request);
        HttpSession session = request.getSession();
        log.debug("user update request: {}, session: {}", body, session);

        User user = (User) session.get(HttpSession.USER_KEY);
        log.debug("session user: {}", user);
        try {
            if (body.deleteProfileImage)
                user.deleteProfileImage(uploader);
            user.update(body.currentPassword(), body.name(), body.newPassword(), body.profileImagePath());
        } catch (IllegalArgumentException e) {
            log.debug("user update error", e);
            return renderPageWithError();
        }
        userDao.saveUser(user);

        request.getSession().set(HttpSession.USER_KEY, user);

        return HttpResponse.redirect(INDEX.path);
    }

    private HttpResponse renderPageWithError() {
        HttpResponse response = new HttpResponse(HttpStatusCode.UNAUTHORIZED);
        ModelAndTemplate modelAndTemplate = new ModelAndTemplate(TEMPLATE_NAME);
        modelAndTemplate.setError("현재 비밀번호가 다릅니다.");
        response.renderTemplate(modelAndTemplate);
        return response;
    }

    private UserUpdateRequest parseRequest(HttpRequest request) {
        Multipart multipart = request.getMultipart();
        String name = multipart.getString("name");
        String newPassword = multipart.getString("newPassword");
        String currentPassword = multipart.getString("currentPassword");
        Boolean deleteProfileImage = multipart.getBoolean("deleteProfileImage");
        if (deleteProfileImage == null)
            deleteProfileImage = false;
        String profileImagePath = null;
        if (!deleteProfileImage)
            profileImagePath = multipart.saveFile("profileImage", uploader);
        return new UserUpdateRequest(name, newPassword, currentPassword, deleteProfileImage, profileImagePath);
    }

    private record UserUpdateRequest(String name, String newPassword, String currentPassword,
                                     Boolean deleteProfileImage, String profileImagePath) {
    }
}
