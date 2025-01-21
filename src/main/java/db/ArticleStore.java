package db;

import model.Article;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ArticleStore {
    private static ConcurrentMap<String, Article> articles = new ConcurrentHashMap<>();

    public static void addArticle(Article article) {
        articles.put(article.getArticleId(), article);
    }

    public static Optional<Article> findArticleById(String id) {
        return Optional.ofNullable(articles.get(id));
    }

    public static List<Article> findAll() {
        return articles.values().stream().sorted(Comparator.comparing(Article::getArticleId).reversed()).toList();
    }
}
