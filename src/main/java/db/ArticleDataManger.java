package db;

import model.Article;

import java.util.Collection;

public interface ArticleDataManger {
    void addArticle(Article article);

    Collection<Article> findArticlesByUserId(String userId);

}
