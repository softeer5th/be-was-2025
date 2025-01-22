package db;

import db.transaction.Transaction;
import db.transaction.TransactionManager;
import db.transaction.TransactionTemplate;
import model.Article;
import model.User;

public class ArticleInitializer {
    private final ArticleDao articleDao = ArticleDao.getInstance();
    private final TransactionTemplate transactionTemplate = TransactionTemplate.getInstance();
    public void init(){
        transactionTemplate.executeWithoutResult(this::addArticle);
    }
    private void addArticle(Transaction transaction, Object[] args){
        articleDao.save(transaction, 1L, new Article(null, "ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ", new User(1L)));
    }
}
