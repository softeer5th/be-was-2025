package manager;

import db.Database;
import exception.ClientErrorException;
import exception.LoginException;
import model.User;
import request.UserCreateRequest;
import request.UserLoginRequest;

import static exception.ErrorCode.*;

public class UserManager {
    public void createUser(final UserCreateRequest request) {
        if (Database.findUserById(request.userId()) != null)
            throw new ClientErrorException(ALREAD_EXIST_USERID);

        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        Database.addUser(user);
    }

    public void loginUser(final UserLoginRequest userLoginRequest) {
        final User user = GetOrElseThrow(userLoginRequest.userId());
        validPassword(user.getPassword(), userLoginRequest.password());
    }

    private void validPassword(String password, String requestedPassword) {
        if (!password.equals(requestedPassword))
            throw new LoginException(INCORRECT_PASSWORD);
    }

    private User GetOrElseThrow(String userId) {
        if (Database.findUserById(userId) == null)
            throw new LoginException(NO_SUCH_USER_ID);
        return Database.findUserById(userId);
    }
}
