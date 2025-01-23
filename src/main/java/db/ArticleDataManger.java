package db;

import model.Article;
import model.User;

import java.util.Collection;

public interface ArticleDataManger {
    void addArticle(Article article);

    Collection<Article> findArticleByUser(User user);

}
