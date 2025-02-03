package manager;

import db.UserDatabase;
import exception.ClientErrorException;
import exception.LoginException;
import exception.ServerErrorException;
import model.User;
import request.ImageRequest;
import request.UserCreateRequest;
import request.UserLoginRequest;
import util.SessionManager;
import util.UserValidator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static exception.ErrorCode.*;

/**
 * 사용자 관리 클래스.
 * <p>
 * 이 클래스는 사용자와 관련된 기능을 담당합니다. 회원가입, 로그인, 로그아웃,
 * 그리고 세션 관리를 포함한 여러 작업을 처리합니다.
 * </p>
 */
public class UserManager {
    private final SessionManager sessionManager;
    private final UserDatabase userDatabase;
    private static UserManager instance;

    /**
     * 생성자. 사용자 데이터베이스와 세션 관리자 인스턴스를 초기화합니다.
     */
    private UserManager() {
        sessionManager = SessionManager.getInstance();
        userDatabase = UserDatabase.getInstance();
    }

    /**
     * UserManager의 단일 인스턴스를 반환합니다. 싱글턴 패턴을 사용합니다.
     *
     * @return UserManager 인스턴스
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * 새 사용자를 생성합니다. 사용자 아이디가 이미 존재하면 예외를 발생시킵니다.
     *
     * @param request 사용자 생성 요청 객체
     * @throws ClientErrorException 이미 존재하는 사용자 아이디로 인해 예외 발생
     */
    public void createUser(final UserCreateRequest request) {
        if (userDatabase.findUserByUserId(request.userId()).isPresent())
            throw new ClientErrorException(ALREADY_EXIST_USERID);

        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        userDatabase.addUser(user);
    }

    /**
     * 사용자가 로그인합니다. 아이디와 비밀번호를 확인하고, 세션 ID를 생성하여 반환합니다.
     *
     * @param userLoginRequest 사용자 로그인 요청 객체
     * @return 세션 ID
     * @throws LoginException 비밀번호가 일치하지 않거나 사용자 정보가 없을 경우 예외 발생
     */
    public String loginUser(final UserLoginRequest userLoginRequest) {
        final User user = getOrElseThrow(userLoginRequest.userId());
        validPassword(user.getPassword(), userLoginRequest.password());

        return sessionManager.makeAndSaveSessionId(user.getId());
    }

    /**
     * 세션 ID를 이용하여 사용자 정보를 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return 세션에 해당하는 사용자 정보 (없으면 Optional.empty 반환)
     */
    public Optional<User> getUserFromSession(String sessionId) {
        if (sessionId == null)
            return Optional.empty();
        final Integer id = sessionManager.getId(sessionId);
        if (id == null)
            return Optional.empty();

        return userDatabase.findUserById(id);
    }

    /**
     * 사용자가 로그아웃합니다. 세션을 삭제합니다.
     *
     * @param sessionId 세션 ID
     */
    public void logoutUser(final String sessionId) {
        sessionManager.deleteSession(sessionId);
    }

    /**
     * 비밀번호가 일치하는지 확인합니다.
     *
     * @param password          실제 비밀번호
     * @param requestedPassword 사용자가 입력한 비밀번호
     * @throws LoginException 비밀번호가 일치하지 않으면 예외 발생
     */
    private void validPassword(final String password, final String requestedPassword) {
        if (!password.equals(requestedPassword))
            throw new LoginException(INCORRECT_PASSWORD);
    }

    /**
     * 사용자 아이디를 기준으로 사용자를 조회합니다. 해당 아이디가 없으면 예외를 발생시킵니다.
     *
     * @param userId 사용자 아이디
     * @return 사용자 객체
     * @throws LoginException 사용자가 존재하지 않으면 예외 발생
     */
    private User getOrElseThrow(final String userId) {
        return userDatabase.findUserByUserId(userId).orElseThrow(() -> new LoginException(NO_SUCH_USER_ID));
    }

    public void updateProfileImage(User user, ImageRequest request) {
        if (request.fileData().length > 5 * 1024 * 1024)
            throw new ClientErrorException(EXCEED_FILE_SIZE);
        if (request.fileData().length != 2 || request.fileData()[0] != 13 || request.fileData()[1] != 10) {

            final UUID uuid = UUID.randomUUID();
            final String filePath = String.format("%s.%s", uuid, request.fileExtension());
            String file = String.format("src/main/resources/static/img/%s", filePath);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write(request.fileData()); // byte[] 데이터 쓰기
            } catch (IOException e) {
                throw new ServerErrorException(ERROR_WHILE_SAVING_FILE);
            }

            userDatabase.updateProfile(user.getId(), filePath);
            return;
        }
        throw new ClientErrorException(MISSING_FIELD);
    }

    public void deleteProfileImage(User user) {
        userDatabase.deleteProfile(user.getId());
    }

    public User getUserOrElseThrow(int id){
        return userDatabase.findUserById(id)
                .orElseThrow(()-> new ClientErrorException(NO_SUCH_USER_ID));
    }

    public void updateInfo(User user, String name, String password) {
        UserValidator.validateNickname(name);
        UserValidator.validatePassword(password);
        userDatabase.updateInfo(user,name,password);
    }
}
