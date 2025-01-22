package manager;

import db.UserDao;
import db.transaction.Transaction;
import model.User;

public class UserManager {
    private static final UserManager INSTANCE = new UserManager();
    private final UserDao userDao = UserDao.getInstance();

    private UserManager(){}

    public static UserManager getInstance(){
        return INSTANCE;
    }
    public User getUser(Transaction transaction, Object[] args){
        return userDao.findById(transaction, (Long)args[0]).get();
    }


}
