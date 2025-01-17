package manager;

import db.Database;
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
    private final Database database;
    private static UserManager instance;


    private UserManager() {
        sessionManager = SessionManager.getInstance();
        database = Database.getInstance();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void createUser(final UserCreateRequest request) {
        if (database.findUserById(request.userId()) != null)
            throw new ClientErrorException(ALREADY_EXIST_USERID);

        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        database.addUser(user);
    }

    public String loginUser(final UserLoginRequest userLoginRequest) {
        final User user = GetOrElseThrow(userLoginRequest.userId());
        validPassword(user.getPassword(), userLoginRequest.password());

        return sessionManager.makeAndSaveSessionId(userLoginRequest.userId());
    }

    public Optional<String> getNameFromSession(String sessionId) {
        if (sessionId == null)
            return Optional.empty();
        final String userId = sessionManager.getUserId(sessionId);
        if (userId == null)
            return Optional.empty();

        final User user = database.findUserById(userId);
        return Optional.of(user.getName());
    }

    public void logoutUser(final String sessionId) {
        sessionManager.deleteSession(sessionId);
    }

    private void validPassword(final String password, final String requestedPassword) {
        if (!password.equals(requestedPassword))
            throw new LoginException(INCORRECT_PASSWORD);
    }

    private User GetOrElseThrow(final String userId) {
        if (database.findUserById(userId) == null)
            throw new LoginException(NO_SUCH_USER_ID);
        return database.findUserById(userId);
    }
}
