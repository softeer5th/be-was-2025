package db;

import db.transaction.Transaction;
import db.transaction.TransactionTemplate;
import model.User;

public class UserInitializer {
    private final UserDao userDao = UserDao.getInstance();
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();

    public void init(){
        transactionTemplate.executeWithoutResult(this::addUser);
    }

    private void addUser(Transaction transaction, Object[] args){
        userDao.save(transaction, new User(null, "aaaaaa", "12341234", "softeer", "softer@naver.com"));
    }
}
