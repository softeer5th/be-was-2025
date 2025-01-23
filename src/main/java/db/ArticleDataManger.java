package db;

import model.Article;

public interface ArticleDataManger {
    void addArticle(Article article);

    Article findArticlesByUserId(String userId);

}
