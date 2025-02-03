package manager;

import db.ArticleDao;
import db.transaction.Transaction;
import model.Article;

public class ArticleManager {
    private static final ArticleManager INSTANCE = new ArticleManager();
    private final ArticleDao articleDao = ArticleDao.getInstance();
    private ArticleManager(){}

    public static ArticleManager getInstance(){
        return INSTANCE;
    }

    public Article getArticle(Transaction transaction, Object[] args){
        Integer page = (Integer) args[0];
        Integer size = (Integer) args[1];
        return articleDao.findArticlesWithPagination(transaction, page, size).get();
    }
}
