package manager;

import db.UserDatabase;
import exception.ClientErrorException;
import exception.LoginException;
import model.User;
import request.UserCreateRequest;
import request.UserLoginRequest;
import util.SessionManager;

import java.util.Optional;

import static exception.ErrorCode.*;

public class UserManager {
    private final SessionManager sessionManager;
    private final UserDatabase userDatabase;
    private static UserManager instance;


    private UserManager() {
        sessionManager = SessionManager.getInstance();
        userDatabase = UserDatabase.getInstance();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void createUser(final UserCreateRequest request) {
        if (userDatabase.findUserByUserId(request.userId()).isPresent())
            throw new ClientErrorException(ALREADY_EXIST_USERID);

        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        userDatabase.addUser(user);
    }

    public String loginUser(final UserLoginRequest userLoginRequest) {
        final User user = getOrElseThrow(userLoginRequest.userId());
        validPassword(user.getPassword(), userLoginRequest.password());

        return sessionManager.makeAndSaveSessionId(user.getId());
    }

    public Optional<Integer> getIdFromSession(String sessionId) {
        if (sessionId == null)
            return Optional.empty();
        final Integer id = sessionManager.getId(sessionId);
        if (id == null)
            return Optional.empty();

        return Optional.of(id);
    }

    public Optional<User> getUserFromSession(String sessionId) {
        if (sessionId == null)
            return Optional.empty();
        final Integer id = sessionManager.getId(sessionId);
        if (id == null)
            return Optional.empty();

        return userDatabase.findUserById(id);
    }

    public void logoutUser(final String sessionId) {
        sessionManager.deleteSession(sessionId);
    }

    private void validPassword(final String password, final String requestedPassword) {
        if (!password.equals(requestedPassword))
            throw new LoginException(INCORRECT_PASSWORD);
    }

    private User getOrElseThrow(final String userId) {
        return userDatabase.findUserByUserId(userId).orElseThrow(() -> new LoginException(NO_SUCH_USER_ID));
    }
}
