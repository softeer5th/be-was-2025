package db;

import model.Article;

import java.util.List;

public interface ArticleDataManger {
    void addArticle(Article article);

    List<Article> findArticlesByUserId(String userId);

}
