package manager;

import db.Database;
import exception.ClientErrorException;
import model.User;
import request.UserCreateRequest;

import static exception.ErrorCode.ALREAD_EXIST_USERID;

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
}
